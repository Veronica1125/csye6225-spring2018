echo "Enter the CICD stack you want to delete"
read STACK_NAME

aws cloudformation delete-stack --stack-name $STACK_NAME
