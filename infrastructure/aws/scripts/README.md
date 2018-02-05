# Instructions to Run the Script
<p>"csye6225-aws-networking-setup.sh" Script will</p>
<ul>
	<li>create and configure required networking resources using AWS CLI. Script should take STACK_NAME as parameter</li>
	<li>Create a Virtual Private Cloud (VPC) resource called STACK_NAME-csye6225-vpc</li>
	<li>Create Internet Gateway resource called STACK_NAME-csye6225-InternetGateway</li>
	<li>Attach the Internet Gateway to STACK_NAME-csye6225-vpc VPC</li>
	<li>Create a public Route Table called STACK_NAME-csye6225-public-route-table</li>
	<li>Create a public route in STACK_NAME-csye6225-public-route-table route table with destination CIDR block 0.0.0.0/0 and STACK_NAME-csye6225-InternetGateway as the target</li>
</ul>
<p>"csye6225-aws-networking-teardown.sh" Script will</p>
<ul>
	<li>delete networking resources using AWS CLI. Script should take STACK_NAME as parameter</li>
</ul>
