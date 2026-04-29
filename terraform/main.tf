terraform {
  required_providers {
    mongodbatlas = {
      source  = "mongodb/mongodbatlas"
      version = "1.12.0"
    }
  }
}

provider "mongodbatlas" {
  public_key  = "Put_Your_Key_Here"
  private_key = "Put_Your_Key_Here"
}

resource "mongodbatlas_project" "project" {
  name   = "AccentureFranchiseProject"
  org_id = "Put_Your_Org_Id_Here"
}

resource "mongodbatlas_cluster" "cluster" {
  project_id   = mongodbatlas_project.project.id
  name         = "franchise-cluster"
  cluster_type = "REPLICASET"
  replication_specs {
    num_shards = 1
    regions_config {
      region_name     = "US_EAST_1"
      electable_nodes = 3
      priority        = 7
      read_only_nodes = 0
    }
  }
  cloud_backup      = false
  auto_scaling_compute_enabled = false
  provider_name               = "TENANT"
  backing_provider_name       = "AWS"
  provider_region_name        = "US_EAST_1"
  provider_instance_size_name = "M0" # Free Tier
}
