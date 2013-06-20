# Usage

## Install Vagrant and some Vagrant Plugins

Follow [Vagrant instructions](http://www.vagrantup.com) in order to install Vagrant.

Then, install some few plugins, required in order to set up a complete environment:
```
vagrant plugin install vagrant-cachier
```

## Install necessary gems

```
gem install bundler
bundle install
```

## Install required Chef cookbooks

```
librarian-chef install
```

## Start VM

```
vagrant up
```
