set -e

#Author Yang Yuan
echo "Author: Yang Yuan"
echo "		yuan.yang@husky.neu.edu"


STACK_NAME=$1
echo "The stack you want to delete: "

#Query the stack
aws cloudformation describe-stacks --stack-name $STACK_NAME

#Delete the cloudformation stack
aws cloudformation delete-stack --stack-name $STACK_NAME


#Job Done
echo "Job done!"
