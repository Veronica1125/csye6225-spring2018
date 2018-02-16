set -e

#Author: Xiao Li
echo "Author: Xiao Li"
echo "        li.xiao5@husky.neu.edu"

#Usage: setting up our networking resources such as Virtual Private Cloud (VPC), Internet Gateway, Route Table and Routes

#Arguments: STACK_NAME

STACK_NAME=$1
#Create VPC and get its Id
vpcId=`aws ec2 create-vpc --cidr-block 10.0.0.0/16 --query 'Vpc.VpcId' --output text`
#Tag vpc
aws ec2 create-tags --resources $vpcId --tags Key=Name,Value=$STACK_NAME-csye6225-vpc
echo "Vpc created-> Vpc Id:  "$vpcId

#Create Internet Gateway
gatewayId=`aws ec2 create-internet-gateway --query 'InternetGateway.InternetGatewayId' --output text`
#Tag Internet Gateway
aws ec2 create-tags --resources $gatewayId --tags Key=Name,Value=$STACK_NAME-csye6225-InternetGateway
echo "Internet gateway created-> gateway Id: "$gatewayId

#Attach Internet Gateway to Vpc
aws ec2 attach-internet-gateway --internet-gateway-id $gatewayId --vpc-id $vpcId
echo "Attached Internet gateway: "$gatewayId" to Vpc: "$vpcId

#Create Route Table 1
routeTableId1=`aws ec2 create-route-table --vpc-id $vpcId --query 'RouteTable.RouteTableId' --output text`
#Tag Route Table
aws ec2 create-tags --resources $routeTableId1 --tags Key=Name,Value=$STACK_NAME-csye6225-public-route-table
echo "Public Route table created -> route table Id1: "$routeTableId1

#Create Route Table 2
routeTableId2=`aws ec2 create-route-table --vpc-id $vpcId --query 'RouteTable.RouteTableId' --output text`
#Tag Route Table
aws ec2 create-tags --resources $routeTableId2 --tags Key=Name,Value=$STACK_NAME-csye6225-private-route-table
echo "Private Route table created -> route table Id2: "$routeTableId2

#Create Route
aws ec2 create-route --route-table-id $routeTableId1 --destination-cidr-block 0.0.0.0/0 --gateway-id $gatewayId
echo "Route created: in "$routeTableId1" target to "$gatewayId


#Create Subnet 1
subnetId1=`aws ec2 create-subnet --vpc-id $vpcId --cidr-block 10.0.0.0/24 --query 'Subnet.SubnetId' --output text`
#Tag Subnet 1
aws ec2 create-tags --resources $subnetId1 --tags Key=Name,Value=$STACK_NAME-csye6225-public-subnet
echo "Public Subnet created -> subnet Id1: "$subnetId1


#Create Subnet 2
subnetId2=`aws ec2 create-subnet --vpc-id $vpcId --cidr-block 10.0.1.0/24 --query 'Subnet.SubnetId' --output text`
#Tag Subnet 2
aws ec2 create-tags --resources $subnetId2 --tags Key=Name,Value=$STACK_NAME-csye6225-private-subnet
echo "Private Subnet created -> subnet Id2: "$subnetId2

#Associate Public Route Table with Public Subnet
aws ec2 associate-route-table --route-table-id $routeTableId1 --subnet-id $subnetId1

#Associate Private Route Table with Private Subnet
aws ec2 associate-route-table --route-table-id $routeTableId2 --subnet-id $subnetId2



#Job Done
echo "Job Done!"




