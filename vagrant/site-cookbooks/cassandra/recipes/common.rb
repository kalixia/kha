%w(cassandra.yaml).each do |f|
  template File.join(node["cassandra"]["conf_dir"], f) do
    source "#{f}.erb"
    owner node["cassandra"]["user"]
    group node["cassandra"]["user"]
    mode  0644
    variables :cassandra => node[:cassandra]
    notifies :restart, resources(:service => "cassandra")
  end
end