output "backend_instance_public_ip" {
  description = "Public IP of the backend EC2 instance"
  value       = aws_instance.backend.public_ip
}

output "backend_instance_public_dns" {
  description = "Public DNS of the backend EC2 instance"
  value       = aws_instance.backend.public_dns
}

output "backend_security_group_id" {
  description = "Security Group ID used by the backend instance"
  value       = aws_security_group.backend_sg.id
}

output "s3_website_endpoint" {
  description = "S3 website endpoint (if created)"
  value       = try(aws_s3_bucket_website_configuration.frontend_site[0].website_endpoint, null)
}

output "dynamodb_table_name" {
  description = "DynamoDB table name (if created)"
  value       = try(aws_dynamodb_table.generation_history[0].name, null)
}

output "dynamodb_table_arn" {
  description = "DynamoDB table ARN (if created)"
  value       = try(aws_dynamodb_table.generation_history[0].arn, null)
}
