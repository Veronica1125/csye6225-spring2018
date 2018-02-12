set -e
#Author Yichuan Zhang
echo "Author: Yichuan Zhang"
echo "		zhang.yichu@husky.neu.edu"

STACK_NAME=$1
echo "The stack you want to delete: "

#Query the stack
aws cloudformation describe-stacks --stack-name $STACK_NAME

#Delete the cloudformation stack
aws cloudformation delete-stack --stack-name $STACK_NAME

#Job Done
echo "Job done!"
