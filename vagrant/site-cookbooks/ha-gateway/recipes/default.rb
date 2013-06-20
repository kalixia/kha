#
# Cookbook Name:: ha-gateway
# Recipe:: default
#
# Copyright 2013, StepInfo
#
# All rights reserved - Do Not Redistribute
#
debFile = "ha-gateway_1.0-SNAPSHOT_all.deb"

include_recipe "java"

directory "/var/chef-package-cache" do
	owner "root"
	group "root"
end

cookbook_file "/var/chef-package-cache/#{debFile}" do
    owner "root"
    group "root"
    mode "0444"
end

package '#{debFile}' do
	provider Chef::Provider::Package::Dpkg
	source "/var/chef-package-cache/#{debFile}"
	action :install
end