default[:cassandra] = {
  :cluster_name => "Test Cluster",
  :initial_token => "",
  :user => "cassandra",
  :jvm  => {
    :xms => 32,
    :xmx => 512
  },
  :limits => {
    :memlock => 'unlimited',
    :nofile  => 48000
  },
  :conf_dir         => "/etc/cassandra/",
  # commit log, data directory, saved caches and so on are all stored under the data root. MK.
  :data_dirs        => ["/var/lib/cassandra/data"],
  :commitlog_dir    => "/var/lib/cassandra/commitlog",
  :saved_caches_dir => "/var/lib/cassandra/saved_caches",
  :log_dir          => "/var/log/cassandra/",
  :listen_address   => node[:ipaddress],
  :rpc_address      => node[:ipaddress],
  :seeds            => [ "127.0.0.1", node[:ipaddress] ],
  :max_heap_size    => nil,
  :heap_new_size    => nil,
  :vnodes           => false,
  :seeds            => [],
  :concurrent_reads => 32,
  :concurrent_writes => 32,
  :snitch           => 'SimpleSnitch'
}