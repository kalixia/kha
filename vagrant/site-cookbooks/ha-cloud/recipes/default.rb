#
# Cookbook Name:: ha-cloud
# Recipe:: default
#
# Copyright 2013, StepInfo
#
# All rights reserved - Do Not Redistribute
#

# Create virtual host for Nginx
include_recipe 'nginx'

template "#{node['nginx']['dir']}/sites-available/ha-cloud" do
  source "ha-cloud.erb"
  owner "root"
  group "root"
  mode 00644
  notifies :reload, 'service[nginx]'
end

nginx_site 'ha-cloud' do
  #enable 'true'
end