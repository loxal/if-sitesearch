provider "hcloud" {
  token = "${file("~/.ssh/hetzner-api-token-analyze-law.txt")}"
}

resource "hcloud_floating_ip" "main" {
  type = "ipv4"
  server_id = "${hcloud_server.node.id}"
  description = "DNS A record"
  home_location = "nbg1"

  provisioner "local-exec" {
    command = "echo ${hcloud_floating_ip.main.ip_address}  >> applied-main.txt"
  }
}

resource "hcloud_server" "node" {
  name = "node-${count.index}"
  count = "1"
  datacenter = "nbg1-dc3"
  image = "ubuntu-18.04"
  server_type = "cx11-ceph"
  ssh_keys = [
    "${hcloud_ssh_key.minion.id}"
  ]


  provisioner "file" "al-license" {
    source = "~/my/project/intrafind/docker-container/tagging-service/intrafind.lic"
    destination = "al-tagger.lic"
  }

  provisioner "file" "if-jdk" {
    source = "~/Desktop/al-tagger/if-meta-jdk-installer-1.8.0.161.bin"
    destination = "if-meta-jdk-installer-1.8.0.161.bin"
  }

  provisioner "file" "if-base-installer" {
    source = "~/Desktop/al-tagger/if-sv-base-installer-1.8.2.17-46323.bin"
    destination = "if-sv-base-installer-1.8.2.17-46323.bin"
  }

  provisioner "file" "if-converter-installer" {
    source = "~/Desktop/al-tagger/if-sv-converter-installer-3.7.0.3-52525.bin"
    destination = "if-sv-converter-installer-3.7.0.3-52525.bin"
  }

  provisioner "file" "if-tagging-installer" {
    source = "~/Desktop/al-tagger/if-sv-tagging-installer-2.6.0.5-51045.bin"
    destination = "if-sv-tagging-installer-2.6.0.5-51045.bin"
  }

  provisioner "file" "al-clause-detection" {
    source = "~/.m2/repository/com/intrafind/if-sv-clausedetection/0.0.0.2-SNAPSHOT/if-sv-clausedetection-0.0.0.2-SNAPSHOT.jar"
    destination = "if-sv-clausedetection-0.0.0.2-SNAPSHOT.jar"
  }

  provisioner "file" "al-tagger-resources" {
    //    tar --exclude .git --exclude .idea -zcvf if-sv-tagging-resources.tgz if-sv-tagging-resources
    source = "~/my/project/intrafind/if-sv-tagging-resources.tgz"
    destination = "if-sv-tagging-resources.tgz"
  }

  provisioner "file" "if-converter-config" {
    source = "/home/intrafind/services/if-sv-converter/config.cfg"
    destination = "if-converter-config.cfg"
  }

  provisioner "file" "contract-example" {
    source = "/home/alex/Downloads/20180726152906.docx"
    destination = "20180726152906.docx"
  }

  provisioner "remote-exec" {
    inline = [
      "apt-get update && apt-get install python -y",
      "chmod +x /root/*.bin",
      "./if-meta-jdk-installer-*.bin -s",
      "./if-sv-base-installer-*.bin -l al-tagger.lic -s",
      "./if-sv-converter-installer-*.bin -l al-tagger.lic -s",
      "tar xfz if-sv-tagging-resources.tgz; mv if-sv-tagging-resources resources",
      "./if-sv-tagging-installer-*.bin -l al-tagger.lic -r /root/resources -s",
      "cp if-sv-clausedetection-*-SNAPSHOT.jar /home/intrafind/services/if-sv-tagging/",
      "/etc/init.d/if-sv-base restart",
      "mv if-converter-config.cfg /home/intrafind/services/if-sv-converter/config.cfg",
      "/etc/init.d/if-sv-converter restart",
      "export INTRAFIND_LICENSE=/root/al-tagger.lic; export INTRAFIND_LICENSE=/root/al-tagger.lic && /etc/init.d/if-sv-tagging restart",
    ]
  }

  provisioner "local-exec" {
    command = "echo ${hcloud_server.node.ipv4_address} > applied-node.txt"
  }
}

resource "hcloud_ssh_key" "minion" {
  name = "minion"
  public_key = "${file("~/.ssh/if-minion-id_rsa.pub")}"
}

