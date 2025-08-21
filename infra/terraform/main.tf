locals {
  tags = {
    Project = var.project_name
    Managed = "terraform"
  }
}

# VPC
resource "aws_vpc" "main" {
  cidr_block           = var.vpc_cidr
  enable_dns_support   = true
  enable_dns_hostnames = true
  tags = merge(local.tags, { Name = "${var.project_name}-vpc" })
}

resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.main.id
  tags   = merge(local.tags, { Name = "${var.project_name}-igw" })
}

resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = var.public_subnet_cidr
  availability_zone       = var.subnet_az
  map_public_ip_on_launch = true
  tags                    = merge(local.tags, { Name = "${var.project_name}-public-subnet" })
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }
  tags = merge(local.tags, { Name = "${var.project_name}-public-rt" })
}

resource "aws_route_table_association" "public_assoc" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public.id
}

# Security Group for backend
resource "aws_security_group" "backend_sg" {
  name        = "${var.project_name}-backend-sg"
  description = "Security group for Learning Tool backend"
  vpc_id      = aws_vpc.main.id

  # HTTP for Spring Boot
  ingress {
    description = "App port"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # SSH (optional; restrict in production)
  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.allow_ssh_cidr]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(local.tags, { Name = "${var.project_name}-backend-sg" })
}

# IAM Role for SSM access (Session Manager)
resource "aws_iam_role" "ssm_role" {
  name               = "${var.project_name}-ec2-ssm-role"
  assume_role_policy = data.aws_iam_policy_document.ec2_assume_role.json
  tags               = local.tags
}

data "aws_iam_policy_document" "ec2_assume_role" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }
  }
}

resource "aws_iam_role_policy_attachment" "ssm_core" {
  role       = aws_iam_role.ssm_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

resource "aws_iam_instance_profile" "ssm_profile" {
  # Use a prefix to avoid conflicts if an instance profile with the fixed name already exists
  name_prefix = "${var.project_name}-ec2-ssm-profile-"
  role        = aws_iam_role.ssm_role.name
}

# Find latest Amazon Linux 2023 AMI
data "aws_ami" "al2023" {
  most_recent = true
  owners      = ["137112412989"] # Amazon
  filter {
    name   = "name"
    values = ["al2023-ami-*-x86_64"]
  }
}

# EC2 instance for backend
resource "aws_instance" "backend" {
  ami                    = data.aws_ami.al2023.id
  instance_type          = var.instance_type
  subnet_id              = aws_subnet.public.id
  vpc_security_group_ids = [aws_security_group.backend_sg.id]
  iam_instance_profile   = aws_iam_instance_profile.ssm_profile.name

  user_data = file("${path.module}/user_data.sh")

  # Optional key for SSH
  key_name = length(var.key_pair_name) > 0 ? var.key_pair_name : null

  tags = merge(local.tags, { Name = "${var.project_name}-backend" })
}

# Optional: S3 static website bucket for frontend
resource "random_id" "suffix" {
  byte_length = 3
}

resource "aws_s3_bucket" "frontend" {
  count  = var.create_s3_website ? 1 : 0
  bucket = "${var.project_name}-frontend-${random_id.suffix.hex}"
  tags   = local.tags
}

resource "aws_s3_bucket_website_configuration" "frontend_site" {
  count  = var.create_s3_website ? 1 : 0
  bucket = aws_s3_bucket.frontend[0].id
  index_document {
    suffix = "index.html"
  }
  error_document {
    key = "index.html"
  }
}

# Block public ACLs and policies by default
resource "aws_s3_bucket_public_access_block" "frontend_block" {
  count  = var.create_s3_website ? 1 : 0
  bucket = aws_s3_bucket.frontend[0].id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}
