#
# Cookbook Name:: datastax-cassandra
# Recipe:: default
#
# Copyright 2013, StepInfo
#
# All rights reserved - Do Not Redistribute
#
include_recipe 'java'

include_recipe 'cassandra::cassandra'