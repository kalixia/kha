include_recipe 'apt'

# Declare DataStax debian packages repository
apt_repository 'datastax-community' do
	uri 'http://debian.datastax.com/community'
	distribution 'stable'
	components [ 'main' ]
	key 'http://debian.datastax.com/debian/repo_key'
	action :add
end

# Install package
package 'dsc12' do
	action :install
end

# Ensure service is enabled and started
service "cassandra" do
  supports :restart => true, :status => true
  action [:enable, :start]
end

include_recipe 'cassandra::jna'
include_recipe 'cassandra::common'