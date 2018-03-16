set -e

echo "Type in the application stack you want to delete"
read STACK_NAME
echo "The stack you want to delete: "

#Query the stack
aws cloudformation describe-stacks --stack-name $STACK_NAME

#Empty the S3 Bucket
ACCOUNT_ID=$(aws sts get-caller-identity --query "Account" --output text)

aws s3 rm s3://${ACCOUNT_ID}s3-code-deploy-csye6225.com --recursive
#Delete the cloudformation stack
aws cloudformation delete-stack --stack-name $STACK_NAME

#Job Done
echo "Job done!"
