-- Adminer 4.3.1 MySQL dump

SET NAMES utf8;
SET foreign_key_checks = 0;

DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `detailed_address` varchar(1024) DEFAULT NULL,
  `landmark` varchar(255) DEFAULT NULL,
  `pincode` varchar(255) DEFAULT NULL,
  `state` varchar(255) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `city_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqljept46mixryf9gklkr3ht0a` (`created_by_id`),
  KEY `FKfcpe5capw9jfeupgx9nbgh5iu` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `alerts_last_seen_time`;
CREATE TABLE `alerts_last_seen_time` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `last_seen_at` bigint(20) DEFAULT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `alst_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `app_login_restricted_location`;
CREATE TABLE `app_login_restricted_location` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_id` varchar(40) NOT NULL,
  `login_restriction_type` varchar(40) NOT NULL,
  `restricted_location_id` bigint(20) DEFAULT NULL,
  `created_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `app_id_restricted_location_id` (`app_id`,`restricted_location_id`),
  KEY `alrl_app_id` (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `asset`;
CREATE TABLE `asset` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `type` varchar(255) NOT NULL,
  `barcode` varchar(255) NOT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  `trip_id` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_asset_1_idx` (`trip_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `autoplan_history`;
CREATE TABLE `autoplan_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `autoplan_id` bigint(20) NOT NULL,
  `consignment_id` bigint(20) NOT NULL,
  `trip_type` varchar(255) DEFAULT NULL,
  `trip_id` bigint(20) DEFAULT NULL,
  `start_location_id` bigint(20) NOT NULL,
  `end_location_id` bigint(20) NOT NULL,
  `route_id` bigint(20) DEFAULT NULL,
  `error_message` varchar(1024) DEFAULT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `event_type` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `autoplan_history_created_at_idx` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `bank_detail`;
CREATE TABLE `bank_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(25) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `code` varchar(50) DEFAULT NULL,
  `status` varchar(25) DEFAULT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `barcode_details`;
CREATE TABLE `barcode_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `client_code` varchar(45) DEFAULT NULL,
  `location_code` varchar(45) DEFAULT NULL,
  `barcode_characters` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `bd_client_code` (`client_code`),
  KEY `bd_location_code` (`location_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `billing_entity`;
CREATE TABLE `billing_entity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  `entity_name` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `FKqljept46mixryiouf9gklkr3ht0a` (`created_by_id`),
  KEY `FKfcpe5capw9jfeupgkj9x9nbgh5iu` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `box`;
CREATE TABLE `box` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `bar_code` varchar(255) DEFAULT NULL,
  `cnote` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `consignment_id` bigint(20) DEFAULT NULL,
  `is_dummy` tinyint(4) NOT NULL DEFAULT '0',
  `is_active` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `barcode_index` (`bar_code`),
  KEY `box_index5` (`consignment_id`),
  KEY `box_index4` (`created_by_id`),
  KEY `box_last_updated_at` (`last_updated_at`),
  KEY `box_bar_code` (`bar_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `box_history`;
CREATE TABLE `box_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `bar_code` varchar(255) DEFAULT NULL,
  `box_id` bigint(20) DEFAULT NULL,
  `consignment_id` bigint(20) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `bh_bar_code_history` (`bar_code`),
  KEY `bh_box_id` (`box_id`),
  KEY `bh_consignment_id` (`consignment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `bp_trip`;
CREATE TABLE `bp_trip` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `arrival_time` bigint(20) DEFAULT NULL,
  `business_partner_id` bigint(20) DEFAULT NULL,
  `dispatch_time` bigint(20) DEFAULT NULL,
  `driver_name` varchar(255) DEFAULT NULL,
  `end_reading` double DEFAULT NULL,
  `from_location_id` bigint(20) DEFAULT NULL,
  `instructions` varchar(255) DEFAULT NULL,
  `mode_of_transfer` varchar(255) DEFAULT NULL,
  `scheduled_dispatch_time` bigint(20) DEFAULT NULL,
  `seal_number` varchar(255) DEFAULT NULL,
  `start_reading` double DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `stock_accumulator_id` bigint(20) DEFAULT NULL,
  `to_location_id` bigint(20) DEFAULT NULL,
  `vehicle_number` varchar(255) DEFAULT NULL,
  `vehicle_type` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `feeder_vendor_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6fmeygsgf644u5l1me0mjlo2g` (`created_by_id`),
  KEY `FKsm74ofpt3ohbfiaemmt0ago90` (`last_updated_by_id`),
  KEY `FKhdo41nj48x15y8vfnpmdbubtm` (`business_partner_id`),
  KEY `FKnolmo3o52iwo2xh8figlmt8v6` (`stock_accumulator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `bp_vehicle`;
CREATE TABLE `bp_vehicle` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  `bp_id` bigint(20) DEFAULT NULL,
  `vehicle_type` varchar(50) DEFAULT NULL,
  `vehicle_number` varchar(50) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `business_partner`;
CREATE TABLE `business_partner` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `business_partner_code` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `bp_type` varchar(45) DEFAULT NULL,
  `availability_status` varchar(255) NOT NULL DEFAULT 'AVAILABLE',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_orix79l831ufycj9o7kkw9neb` (`business_partner_code`),
  KEY `FKcfdinl6hmmdpg9o5x3bsaus4b` (`created_by_id`),
  KEY `FKnmc68tqqw054m63q5oiqc24w1` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `business_partner_email`;
CREATE TABLE `business_partner_email` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `business_partner_id` bigint(20) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `report_code` varchar(45) DEFAULT NULL,
  `new_tablecol` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT NULL,
  `created_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `cargonet_unavailability_reason`;
CREATE TABLE `cargonet_unavailability_reason` (
  `id` bigint(20) NOT NULL,
  `reason` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `cashbook_collection`;
CREATE TABLE `cashbook_collection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `status` varchar(50) DEFAULT NULL,
  `total_cn` int(11) DEFAULT NULL,
  `payment_type` varchar(50) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `agreed_amount` double DEFAULT NULL,
  `collected_by_id` bigint(20) DEFAULT NULL,
  `created_location_id` bigint(20) DEFAULT NULL,
  `bank_id` bigint(20) DEFAULT NULL,
  `cheque_number` varchar(255) DEFAULT NULL,
  `reference_number` varchar(255) DEFAULT NULL,
  `is_active` tinyint(4) DEFAULT '1',
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `cashbook_collection_history`;
CREATE TABLE `cashbook_collection_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cashbook_collection_id` bigint(20) NOT NULL,
  `status` varchar(50) DEFAULT NULL,
  `total_cn` int(11) DEFAULT NULL,
  `payment_type` varchar(50) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `agreed_amount` double DEFAULT NULL,
  `collected_by_id` bigint(20) NOT NULL,
  `created_location_id` bigint(20) DEFAULT NULL,
  `bank_id` bigint(20) DEFAULT NULL,
  `cheque_number` varchar(255) DEFAULT NULL,
  `reference_number` varchar(255) DEFAULT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `cashbook_collection_uploaded_files`;
CREATE TABLE `cashbook_collection_uploaded_files` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cashbook_collection_id` bigint(20) NOT NULL,
  `collected_by_id` bigint(20) NOT NULL,
  `s3_url` varchar(1024) DEFAULT NULL,
  `is_active` tinyint(4) DEFAULT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `cashbook_deposit`;
CREATE TABLE `cashbook_deposit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `status` varchar(50) DEFAULT NULL,
  `total_cn` int(11) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `deposited_by_id` bigint(20) NOT NULL,
  `bank_id` bigint(20) DEFAULT NULL,
  `cheque_number` varchar(255) DEFAULT NULL,
  `reference_number` varchar(255) DEFAULT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `is_active` tinyint(4) DEFAULT NULL,
  `agreed_amount` double DEFAULT NULL,
  `last_action_by_id` bigint(20) DEFAULT NULL,
  `created_location_id` bigint(20) DEFAULT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `cashbook_deposit_history`;
CREATE TABLE `cashbook_deposit_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deposit_id` bigint(20) NOT NULL,
  `status` varchar(50) DEFAULT NULL,
  `total_cn` int(11) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `deposited_by_id` bigint(20) NOT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `agreed_amount` double DEFAULT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `cashbook_deposit_uploaded_files`;
CREATE TABLE `cashbook_deposit_uploaded_files` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cashbook_deposit_id` bigint(20) NOT NULL,
  `deposited_by_id` bigint(20) NOT NULL,
  `s3_url` varchar(1024) DEFAULT NULL,
  `is_active` tinyint(4) DEFAULT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `cashbook_dispute_reason`;
CREATE TABLE `cashbook_dispute_reason` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `reason` varchar(255) NOT NULL,
  `is_active` tinyint(4) DEFAULT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `cashbook_remittance`;
CREATE TABLE `cashbook_remittance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `total_cn` int(11) DEFAULT NULL,
  `remittance_mode` varchar(50) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `remitted_by_id` bigint(20) DEFAULT NULL,
  `remittance_date` bigint(20) DEFAULT NULL,
  `transaction_number` varchar(255) DEFAULT NULL,
  `is_active` tinyint(4) DEFAULT '1',
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `cc_attribution`;
CREATE TABLE `cc_attribution` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `consignment_id` bigint(20) NOT NULL,
  `timestamp` varchar(255) NOT NULL,
  `attribution_category` varchar(255) NOT NULL,
  `responsible_ou_id` bigint(20) DEFAULT NULL,
  `responsible_user_id` bigint(20) DEFAULT NULL,
  `responsible_entity` varchar(255) DEFAULT NULL,
  `prev_category` varchar(40) DEFAULT NULL,
  `new_category` varchar(40) DEFAULT NULL,
  `eta_addition` bigint(20) DEFAULT NULL,
  `action_ids` text NOT NULL,
  `meta_data` text NOT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `cca_consignment_id` (`consignment_id`),
  KEY `cca_attribution_category` (`attribution_category`),
  KEY `cca_timestamp` (`timestamp`),
  KEY `cca_responsible_ou_id` (`responsible_ou_id`),
  KEY `cca_responsible_user_id` (`responsible_user_id`),
  KEY `cca_responsible_entity` (`responsible_entity`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `cc_meta_data`;
CREATE TABLE `cc_meta_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `consignment_id` bigint(20) NOT NULL,
  `cnote` varchar(255) NOT NULL,
  `client` text NOT NULL,
  `from_location_admin_cluster` text NOT NULL,
  `to_location_admin_cluster` text NOT NULL,
  `from_pincode_branch` text NOT NULL,
  `to_pincode_branch` text NOT NULL,
  `destination_zone` text NOT NULL,
  `weight` double NOT NULL,
  `charged_weight` double DEFAULT NULL,
  `volume` double DEFAULT NULL,
  `number_of_boxes` int(11) NOT NULL,
  `service_type` varchar(255) DEFAULT NULL,
  `client_promised_delivery` bigint(20) DEFAULT NULL,
  `sunday_booking` tinyint(4) NOT NULL,
  `booking_date_time` bigint(20) NOT NULL,
  `cn_creation_time` bigint(20) DEFAULT NULL,
  `cn_creation_ou` varchar(255) DEFAULT NULL,
  `is_frozen` tinyint(1) NOT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ccmd_consignment_id` (`consignment_id`),
  KEY `ccmd_cnote` (`cnote`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `cc_ou_meta_data`;
CREATE TABLE `cc_ou_meta_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `consignment_id` bigint(20) NOT NULL,
  `ou_id` bigint(20) NOT NULL,
  `cs_sequence` tinyint(2) NOT NULL,
  `ou_tag` varchar(40) DEFAULT NULL,
  `first_action` varchar(40) NOT NULL,
  `first_action_timestamp` bigint(20) NOT NULL,
  `next_ou_id` bigint(20) NOT NULL,
  `dispatch_cutoff` bigint(20) NOT NULL,
  `dispatch_cutoff_trip_type` varchar(40) DEFAULT NULL,
  `dispatch_cutoff_trip_id` bigint(20) DEFAULT NULL,
  `arrival_action_id` bigint(20) DEFAULT NULL,
  `next_possible_trip_id_after_arrival` bigint(20) DEFAULT NULL,
  `next_possible_trip_after_arrival_scheduled_dispatch_time` bigint(20) DEFAULT NULL,
  `next_possible_trip_after_arrival_actual_dispatch_time_zt` bigint(20) DEFAULT NULL,
  `next_possible_trip_after_arrival_actual_dispatch_time_gps` bigint(20) DEFAULT NULL,
  `unload_action_id` bigint(20) DEFAULT NULL,
  `next_possible_trip_id_after_unload` bigint(20) DEFAULT NULL,
  `next_possible_trip_after_unload_scheduled_dispatch_time` bigint(20) DEFAULT NULL,
  `next_possible_trip_after_unload_actual_dispatch_time_zt` bigint(20) DEFAULT NULL,
  `next_possible_trip_after_unload_actual_dispatch_time_gps` bigint(20) DEFAULT NULL,
  `is_arrival_frozen` tinyint(1) DEFAULT NULL,
  `is_unload_frozen` tinyint(1) DEFAULT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `consignment_id_cs_sequence` (`consignment_id`,`cs_sequence`),
  KEY `ccomd_consignment_id` (`consignment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `cc_processed_data`;
CREATE TABLE `cc_processed_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `consignment_id` bigint(20) NOT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `client_id` bigint(20) NOT NULL,
  `old_cpb_category` varchar(40) NOT NULL,
  `new_cpb_category` varchar(40) NOT NULL,
  `change_type` varchar(40) NOT NULL,
  `action_id` bigint(20) NOT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ccpd_consignment_id` (`consignment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `cc_trip_detail`;
CREATE TABLE `cc_trip_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `consignment_id` bigint(20) NOT NULL,
  `cs_sequence` int(11) NOT NULL,
  `trip_id` bigint(20) NOT NULL,
  `trip_type` varchar(50) NOT NULL,
  `trip_status` varchar(50) NOT NULL,
  `start_location_id` bigint(20) NOT NULL,
  `end_location_id` bigint(20) NOT NULL,
  `trip_std_travel_hrs` bigint(20) NOT NULL,
  `trip_std_stop_hrs` bigint(20) NOT NULL,
  `trip_std_stop_split_hrs` varchar(255) NOT NULL,
  `trip_scheduled_start_time` bigint(20) DEFAULT NULL,
  `trip_scheduled_end_time` bigint(20) DEFAULT NULL,
  `trip_actual_start_time_zt` bigint(20) DEFAULT NULL,
  `trip_actual_end_time_zt` bigint(20) DEFAULT NULL,
  `trip_actual_stop_hrs_zt` bigint(20) NOT NULL,
  `trip_actual_stop_split_hrs_zt` varchar(255) DEFAULT NULL,
  `trip_actual_start_time_gps` bigint(20) DEFAULT NULL,
  `trip_actual_end_time_gps` bigint(20) DEFAULT NULL,
  `trip_actual_stop_hrs_gps` bigint(20) NOT NULL,
  `trip_actual_stop_split_hrs_gps` varchar(255) DEFAULT NULL,
  `is_frozen` tinyint(1) NOT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `cctd_consignment_id` (`consignment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `ce_escalation_alerts`;
CREATE TABLE `ce_escalation_alerts` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `is_active` int(11) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `consignment_id` bigint(20) DEFAULT NULL,
  `alertType` varchar(45) DEFAULT NULL,
  `alertID` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `ce_issue_mapper`;
CREATE TABLE `ce_issue_mapper` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ce_issue` varchar(45) NOT NULL DEFAULT 'OU_ESCALATION',
  `ce_sub_issue` varchar(45) NOT NULL,
  `alert_reason_against_sub_issue` varchar(45) NOT NULL,
  `is_active` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `champion_vehicle`;
CREATE TABLE `champion_vehicle` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  `vehicle_type` varchar(50) DEFAULT NULL,
  `vehicle_number` varchar(50) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `city`;
CREATE TABLE `city` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `code` varchar(255) DEFAULT NULL,
  `city_name` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `clients`;
CREATE TABLE `clients` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `billing_name` varchar(255) DEFAULT NULL,
  `client_code` varchar(255) DEFAULT NULL,
  `display_name` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `sales_executive` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `website` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `industry_type_id` bigint(20) DEFAULT NULL,
  `charge_basis` varchar(45) DEFAULT NULL,
  `factor` double DEFAULT NULL,
  `volume_multiplier` double DEFAULT NULL,
  `priority` varchar(10) DEFAULT NULL,
  `service_type` varchar(255) DEFAULT NULL,
  `cnote_type` varchar(255) DEFAULT 'NORMAL',
  `has_billing_entity` tinyint(4) DEFAULT '0',
  `appointment_keyword` varchar(1024) DEFAULT NULL,
  `mall_keyword` varchar(1024) DEFAULT NULL,
  `content_types` varchar(1024) DEFAULT NULL,
  `insurance_reqd` tinyint(4) DEFAULT '0',
  `is_business_agent` tinyint(4) DEFAULT '0',
  `blocked_paid_for_delivery` tinyint(4) DEFAULT '0',
  `blocked_topay_for_delivery` tinyint(4) DEFAULT '0',
  `organization_id` bigint(20) DEFAULT '1',
  `insurance_excluded` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_my8br9xq8531mf0uw4xwj261k` (`client_code`),
  KEY `FKibbfmfn566hafu79hnst7icq` (`created_by_id`),
  KEY `FKr0tgfkpbs9iqbvdtjsokv5ncg` (`last_updated_by_id`),
  KEY `FK7ebbh00t5amwa82g1jk070txw` (`industry_type_id`),
  KEY `cl_organization_id` (`organization_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `client_address`;
CREATE TABLE `client_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `address_type` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `address_id` bigint(20) DEFAULT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  `client_address_code` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `status` varchar(255) NOT NULL,
  `contact_person` varchar(255) DEFAULT NULL,
  `organization_id` bigint(20) DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `FK56fw7amtm20ifqh0f7ptkso1a` (`created_by_id`),
  KEY `FKggd313pbkctudfb2ipwwak0k1` (`last_updated_by_id`),
  KEY `FKdsuhnhv460af4xkakbbttea57` (`address_id`),
  KEY `FKndvyxiewc7oj1dlshobv2tiw0` (`client_id`),
  KEY `ca_client_address_code` (`client_address_code`),
  KEY `ca_organization_id` (`organization_id`),
  KEY `ca_address_type` (`address_type`),
  KEY `ca_status` (`status`),
  KEY `ca_client_id_address_type_status` (`client_id`,`address_type`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `client_insurance_details`;
CREATE TABLE `client_insurance_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(20) NOT NULL,
  `client_code` varchar(45) DEFAULT NULL,
  `client_name` varchar(255) DEFAULT NULL,
  `insurance_holder_name` varchar(45) DEFAULT NULL,
  `insurance_holder_email` varchar(45) DEFAULT NULL,
  `insurance_holder_phone` varchar(45) DEFAULT NULL,
  `insurance_holder_address_id` varchar(45) DEFAULT NULL,
  `is_active` tinyint(4) DEFAULT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  `client_customer_code` varchar(45) DEFAULT NULL,
  `rov_percent` double NOT NULL DEFAULT '0.025',
  `minimum_premium` double NOT NULL DEFAULT '10',
  `from_locations` varchar(255) DEFAULT '15',
  `minimum_premium_fragile` double NOT NULL DEFAULT '10',
  `rov_percent_fragile` double NOT NULL DEFAULT '0.025',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `client_user`;
CREATE TABLE `client_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `client_role` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `FK6p74omnrg7a2w4pnhyuy6jvs5` (`user_id`),
  KEY `FK82w1dbgagg29jmmtmoxb8b9p7` (`created_by_id`),
  KEY `FKb73q74m7iekk1cuf4y9iq0of7` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `client_user_mapping`;
CREATE TABLE `client_user_mapping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` varchar(45) NOT NULL,
  `last_updated_at` varchar(45) NOT NULL,
  `created_by_id` varchar(45) DEFAULT NULL,
  `last_updated_by_id` varchar(45) DEFAULT NULL,
  `client_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `client_address_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_client_user_mapping_1_idx` (`client_id`),
  KEY `fk_client_user_mapping_2_idx` (`user_id`),
  KEY `fk_client_user_mapping_3_idx` (`client_address_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `client_vas_detail`;
CREATE TABLE `client_vas_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(20) DEFAULT NULL,
  `vas_metadata` text DEFAULT NULL,
  `client_vas_type` varchar(45) DEFAULT NULL,
  `is_active` varchar(45) DEFAULT NULL,
  `created_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `pricing_metadata` text DEFAULT NULL,
  `finance_activated` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `cvd_client_id` (`client_id`,`client_vas_type`,`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `completion_details`;
CREATE TABLE `completion_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `consignment_id` bigint(20) NOT NULL,
  `cnote` varchar(45) DEFAULT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `com_det_consignment_id` (`consignment_id`),
  KEY `com_det_consignment_id_index` (`consignment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `consignment`;
CREATE TABLE `consignment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `booking_date_time` bigint(20) DEFAULT NULL,
  `from_pin_code` varchar(255) DEFAULT NULL,
  `charged_weight` double DEFAULT NULL,
  `organization_charged_weight` double DEFAULT NULL,
  `cnote` varchar(255) DEFAULT NULL,
  `consignee_address` varchar(1024) DEFAULT NULL,
  `consigner_address` varchar(1024) DEFAULT NULL,
  `packing` varchar(255) DEFAULT NULL,
  `contents` varchar(255) DEFAULT NULL,
  `drs_id` bigint(20) DEFAULT NULL,
  `bp_trip_id` bigint(20) DEFAULT NULL,
  `dho_id` bigint(20) DEFAULT NULL,
  `pick_up_id` bigint(20) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `to_pin_code` varchar(255) DEFAULT NULL,
  `total_boxes` int(11) DEFAULT NULL,
  `value` double DEFAULT NULL,
  `volume` double DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  `consignee_client_address_id` bigint(20) DEFAULT NULL,
  `consignor_client_address_id` bigint(20) DEFAULT NULL,
  `from_location_id` bigint(20) DEFAULT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  `to_location_id` bigint(20) DEFAULT NULL,
  `originating_form` tinyint(4) DEFAULT NULL,
  `terminating_form` tinyint(4) DEFAULT NULL,
  `client_shipment_code` varchar(45) DEFAULT NULL,
  `barcode_type` varchar(45) DEFAULT 'PRE_PRINTED',
  `prs_id` bigint(20) DEFAULT NULL,
  `organization_id` bigint(20) NOT NULL DEFAULT '1',
  `consignor_name` varchar(255) DEFAULT NULL,
  `consigner_email` varchar(255) DEFAULT NULL,
  `consigner_phone` varchar(255) DEFAULT NULL,
  `consignee_name` varchar(255) DEFAULT NULL,
  `consignee_email` varchar(255) DEFAULT NULL,
  `consignee_phone` varchar(255) DEFAULT NULL,
  `originial_no_of_boxes` int(11) DEFAULT NULL,
  `delivery_date_time` bigint(20) DEFAULT NULL,
  `promised_delivery_date_time` bigint(20) DEFAULT NULL,
  `health` varchar(255) DEFAULT NULL,
  `gst_number` varchar(255) DEFAULT NULL,
  `scheduled_consignment_id` bigint(20) DEFAULT NULL,
  `mapped_to_consignment_id` bigint(20) DEFAULT NULL,
  `service_type` varchar(255) DEFAULT NULL,
  `cnote_type` varchar(255) DEFAULT 'NORMAL',
  `scheduled_pickup_time` bigint(20) DEFAULT NULL,
  `required_documents` varchar(255) DEFAULT NULL,
  `collected_documents` varchar(255) DEFAULT NULL,
  `rescheduled_delivery_date_time` bigint(20) DEFAULT NULL,
  `is_active` tinyint(4) NOT NULL DEFAULT '1',
  `completion_status` varchar(255) NOT NULL,
  `stale_category` varchar(50) DEFAULT NULL,
  `payment_status` varchar(45) DEFAULT 'COMPLETED',
  `actual_weight` double DEFAULT NULL,
  `invoice_weight` double DEFAULT NULL,
  `deps_weight` double DEFAULT NULL,
  `documents_by_bp` varchar(512) DEFAULT NULL,
  `qc_volume` double DEFAULT NULL,
  `epod_added_date_time` bigint(20) DEFAULT NULL,
  `qc_check` tinyint(4) NOT NULL DEFAULT '0',
  `is_fragile` tinyint(4) DEFAULT '0',
  `version` bigint(20) DEFAULT '0',
  `billing_entity` varchar(255) DEFAULT NULL,
  `billing_entity_id` bigint(20) DEFAULT NULL,
  `mall_detail_id` bigint(20) DEFAULT NULL,
  `mall_name` varchar(255) DEFAULT NULL,
  `appointment_required` tinyint(4) DEFAULT '0',
  `stockcheck_reason` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `cnote` (`cnote`),
  KEY `cn_FK5snm8d5qs34h63kmq54iwit5n` (`client_id`),
  KEY `cn_fk_consignment_1_idx` (`consignee_client_address_id`),
  KEY `cn_fk_consignment_2_idx` (`consignor_client_address_id`),
  KEY `cn_fk_consignment_3_idx` (`drs_id`),
  KEY `cn_fk_consignment_4_idx` (`pick_up_id`),
  KEY `cn_fk_consignment_6_idx` (`bp_trip_id`),
  KEY `cn_status_index` (`status`),
  KEY `cn_created_at_index` (`created_at`),
  KEY `cn_delivery_date_time_index` (`delivery_date_time`),
  KEY `cn_consignee_email_index` (`consignee_email`),
  KEY `cn_index18` (`created_by_id`),
  KEY `cn_index19` (`last_updated_by_id`),
  KEY `cn_fk_dho_consignment` (`dho_id`),
  KEY `cn_prs_id_index` (`prs_id`),
  KEY `cn_cnote_index` (`cnote`),
  KEY `cn_is_active` (`is_active`),
  KEY `cn_organization_id` (`organization_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `consignment_appointment`;
CREATE TABLE `consignment_appointment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `consignment_id` bigint(20) DEFAULT NULL,
  `appointment_id` varchar(255) DEFAULT NULL,
  `appointment_time` bigint(20) DEFAULT NULL,
  `is_active` tinyint(4) DEFAULT NULL,
  `appointment_id_required` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ca_consignment_id` (`consignment_id`),
  KEY `ca_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `consignment_checklist`;
CREATE TABLE `consignment_checklist` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `state` varchar(45) NOT NULL,
  `industry_type` varchar(45) NOT NULL,
  `originating_form` varchar(255) DEFAULT NULL,
  `terminating_form` varchar(255) DEFAULT NULL,
  `transit` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `consignment_cod_dod`;
CREATE TABLE `consignment_cod_dod` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `consignment_id` bigint(20) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `collected_by_id` bigint(20) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `payment_type` varchar(45) DEFAULT NULL,
  `in_favour_of` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT NULL,
  `created_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ccd_consignment_id` (`consignment_id`),
  KEY `ccd_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `consignment_document`;
CREATE TABLE `consignment_document` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `consignment_id` bigint(20) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `document` varchar(255) DEFAULT NULL,
  `goods_category` varchar(255) DEFAULT NULL,
  `is_active` tinyint(4) DEFAULT NULL,
  `created_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `document_type` varchar(255) DEFAULT NULL,
  `document_number` varchar(255) DEFAULT NULL,
  `document_validity` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `cd_consignment_id_is_active` (`consignment_id`,`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `consignment_document_checklist`;
CREATE TABLE `consignment_document_checklist` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `state` varchar(255) NOT NULL,
  `industry_type` varchar(255) NOT NULL,
  `from_transit_to` varchar(255) NOT NULL,
  `document` varchar(255) NOT NULL,
  `document_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `consignment_history`;
CREATE TABLE `consignment_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `consignment_id` bigint(20) DEFAULT NULL,
  `manifest_id` bigint(20) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `device_id_type` varchar(45) DEFAULT NULL,
  `device_id` varchar(255) DEFAULT NULL,
  `app_version` varchar(255) DEFAULT NULL,
  `device_type` varchar(45) DEFAULT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `total_no_of_boxes` bigint(20) DEFAULT NULL,
  `original_no_of_boxes` bigint(20) DEFAULT NULL,
  `task_id` bigint(20) DEFAULT NULL,
  `payment_status` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_consignment_history_1_idx` (`client_id`),
  KEY `ch_index4` (`consignment_id`),
  KEY `ch_index5` (`created_by_id`),
  KEY `ch_index6` (`last_updated_by_id`),
  KEY `ch_index7` (`user_id`),
  KEY `ch_last_updated_at` (`last_updated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `consignment_insurance_details`;
CREATE TABLE `consignment_insurance_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `consignment_id` bigint(20) NOT NULL,
  `policy_number` varchar(255) DEFAULT NULL,
  `content_type` varchar(255) DEFAULT NULL,
  `value` double DEFAULT NULL,
  `customer_insurance_id` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `premium` double DEFAULT NULL,
  `consignor_name` varchar(255) DEFAULT NULL,
  `consignor_phone` varchar(255) DEFAULT NULL,
  `error_msg` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `cid_consignment_id` (`consignment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `consignment_life_cycle_order`;
CREATE TABLE `consignment_life_cycle_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `status` varchar(45) NOT NULL,
  `life_cycle_order` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `consignment_schedule`;
CREATE TABLE `consignment_schedule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `consignment_id` bigint(20) NOT NULL,
  `location_type` varchar(40) NOT NULL,
  `location_id` bigint(20) NOT NULL,
  `location_tag` varchar(255) DEFAULT NULL,
  `sequence` tinyint(2) NOT NULL,
  `plan_count` int(11) NOT NULL,
  `plan_status` varchar(40) NOT NULL,
  `arrival_scheduled_time` bigint(20) DEFAULT NULL,
  `arrival_cutoff_time` bigint(20) DEFAULT NULL,
  `arrival_time` bigint(20) DEFAULT NULL,
  `marked_in_by_id` bigint(20) DEFAULT NULL,
  `preferred_departure_trip_type` varchar(40) DEFAULT NULL,
  `preferred_departure_trip_id` bigint(20) DEFAULT NULL,
  `departure_trip_type` varchar(40) DEFAULT NULL,
  `departure_trip_id` bigint(20) DEFAULT NULL,
  `departure_manifest_id` bigint(20) DEFAULT NULL,
  `planning_type` varchar(40) DEFAULT NULL,
  `cpb_category` varchar(40) DEFAULT NULL,
  `planned_at` bigint(20) DEFAULT NULL,
  `planned_by_id` bigint(20) DEFAULT NULL,
  `loaded_at` bigint(20) DEFAULT NULL,
  `loaded_by_id` bigint(20) DEFAULT NULL,
  `departure_scheduled_time` bigint(20) DEFAULT NULL,
  `departure_cutoff_trip_type` varchar(40) DEFAULT NULL,
  `departure_cutoff_trip_id` bigint(20) DEFAULT NULL,
  `departure_cutoff_time` bigint(20) DEFAULT NULL,
  `departure_time` bigint(20) DEFAULT NULL,
  `marked_out_by_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `alert_snooze_category` varchar(40) DEFAULT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `version` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `cs_index2` (`location_type`,`location_id`),
  KEY `cs_index3` (`consignment_id`),
  KEY `cs_index4` (`departure_trip_type`,`departure_trip_id`),
  KEY `cs_index5` (`departure_manifest_id`),
  KEY `cs_departure_trip_id` (`departure_trip_id`),
  KEY `cs_sequence` (`sequence`),
  KEY `cs_is_active` (`is_active`),
  KEY `cs_last_updated_at` (`last_updated_at`),
  KEY `cs_consignment_id_location_type_is_active_plan_status` (`consignment_id`,`location_type`,`is_active`,`plan_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `consignment_schedule_cache`;
CREATE TABLE `consignment_schedule_cache` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `consignment_id` bigint(20) NOT NULL,
  `location_type` varchar(40) NOT NULL,
  `location_id` bigint(20) NOT NULL,
  `trip_type` varchar(40) DEFAULT NULL,
  `trip_id` bigint(20) DEFAULT NULL,
  `manifest_id` bigint(20) DEFAULT NULL,
  `to_location_type` varchar(40) DEFAULT NULL,
  `to_location_id` bigint(20) DEFAULT NULL,
  `planning_type` varchar(40) DEFAULT NULL,
  `cpb_category` varchar(40) DEFAULT NULL,
  `loaded_at` bigint(20) DEFAULT NULL,
  `loaded_by_id` bigint(20) DEFAULT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) NOT NULL DEFAULT '57',
  `last_updated_by_id` bigint(20) NOT NULL DEFAULT '57',
  `version` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `csc_consignment_id_location_type_location_id` (`consignment_id`,`location_type`,`location_id`),
  KEY `csc_trip_id` (`trip_id`),
  KEY `csc_manifest_id` (`manifest_id`),
  KEY `csc_consignment_id_index` (`consignment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `consignment_unclean_remarks_record`;
CREATE TABLE `consignment_unclean_remarks_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `reason` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `consignment_unclean_uploaded_files_record`;
CREATE TABLE `consignment_unclean_uploaded_files_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `consignment_id` bigint(20) NOT NULL,
  `consignment_upload_file_id` bigint(20) DEFAULT NULL,
  `remarks` varchar(1024) NOT NULL,
  `comments` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `cuufr_consignment_upload_file_id_index` (`consignment_upload_file_id`),
  KEY `cuufr_consignment_id` (`consignment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `consignment_uploaded_files`;
CREATE TABLE `consignment_uploaded_files` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `consignment_id` bigint(20) NOT NULL,
  `fileName` varchar(255) DEFAULT NULL,
  `file_types` varchar(255) DEFAULT NULL,
  `s3URL` varchar(2048) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `short_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1ai1o1hyt35cdgvx433f3s8hi` (`consignment_id`),
  KEY `cuf_index3` (`created_by_id`),
  KEY `cuf_index4` (`last_updated_by_id`),
  KEY `cuf_consignment_id_file_types` (`consignment_id`,`file_types`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `content_type`;
CREATE TABLE `content_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `content` varchar(255) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `continuum`;
CREATE TABLE `continuum` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `consignment_id` bigint(20) NOT NULL,
  `timestamp` bigint(20) NOT NULL,
  `action` varchar(40) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `user_email` varchar(255) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `user_mobile_number` varchar(15) DEFAULT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  `location_type` varchar(40) DEFAULT NULL,
  `location_code` varchar(255) DEFAULT NULL,
  `location_name` varchar(255) DEFAULT NULL,
  `cs_sequence` tinyint(2) DEFAULT NULL,
  `cpb_category` varchar(40) DEFAULT NULL,
  `phase` varchar(40) NOT NULL,
  `scheduled_action_time` bigint(20) DEFAULT NULL,
  `cutoff_action_time` bigint(20) DEFAULT NULL,
  `previous_eta` bigint(20) DEFAULT NULL,
  `new_eta` bigint(20) DEFAULT NULL,
  `is_frozen` tinyint(1) NOT NULL,
  `meta_data` text DEFAULT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `cc_consignment_id` (`consignment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `damage_pilferage_record`;
CREATE TABLE `damage_pilferage_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `depsType` varchar(255) NOT NULL,
  `cnote` varchar(255) DEFAULT NULL,
  `box_id` bigint(20) DEFAULT NULL,
  `damage_type` varchar(255) DEFAULT NULL,
  `damage_reason` varchar(255) DEFAULT NULL,
  `pilferage_type` varchar(255) DEFAULT NULL,
  `pilferage_reason` varchar(255) DEFAULT NULL,
  `comments` varchar(255) DEFAULT NULL,
  `deps_final_status` varchar(255) DEFAULT NULL,
  `damage_pilferage_resolution_action` varchar(255) DEFAULT NULL,
  `resolution_steps` varchar(255) DEFAULT NULL,
  `barcode` varchar(255) NOT NULL,
  `damage_pilferage_url` varchar(255) DEFAULT NULL,
  `resolution_url` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `location_id` bigint(20) NOT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `dpr_client_id_fk` (`client_id`),
  KEY `dpr_index4` (`created_by_id`),
  KEY `dpr_index5` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `delivery_handover`;
CREATE TABLE `delivery_handover` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `assigned_user_id` bigint(20) DEFAULT NULL,
  `from_location_id` bigint(20) DEFAULT NULL,
  `to_location_id` bigint(20) DEFAULT NULL,
  `status` varchar(40) NOT NULL,
  `organization_id` bigint(20) NOT NULL,
  `max_weight` double NOT NULL,
  `max_volume` double NOT NULL,
  `scheduled_closing_time` bigint(20) NOT NULL,
  `scheduled_start_time` bigint(20) NOT NULL,
  `loading_end_time` bigint(20) DEFAULT NULL,
  `loading_start_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `dh_df_status_to_location` (`organization_id`,`to_location_id`,`status`),
  KEY `dh_df_status` (`organization_id`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `delivery_run_sheet`;
CREATE TABLE `delivery_run_sheet` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `scheduled_dispatch_time` bigint(20) DEFAULT NULL,
  `dispatch_time` bigint(20) DEFAULT NULL,
  `dispatched_at` bigint(20) DEFAULT NULL,
  `dispatched_by_id` bigint(20) DEFAULT NULL,
  `instructions` varchar(1024) DEFAULT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `vehicle_number` varchar(255) DEFAULT NULL,
  `vehicle_type` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `drs_created_at_index` (`created_at`),
  KEY `drs_status_index` (`status`),
  KEY `drs_index6` (`created_by_id`),
  KEY `drs_index7` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `delivery_run_sheet_history`;
CREATE TABLE `delivery_run_sheet_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `business_partner_id` bigint(20) DEFAULT NULL,
  `delivery_run_sheeet_id` bigint(20) DEFAULT NULL,
  `dispatch_time` bigint(20) DEFAULT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `vehicle_number` varchar(255) DEFAULT NULL,
  `vehicle_type` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKeks6mg9k3n1pnwl7tw5l25dt3` (`business_partner_id`),
  KEY `drsh_index4` (`created_by_id`),
  KEY `drsh_index5` (`last_updated_by_id`),
  KEY `drsh_delivery_run_sheeet_id` (`delivery_run_sheeet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `deps_document`;
CREATE TABLE `deps_document` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deps_id` bigint(20) NOT NULL,
  `deps_document_type` enum('DEPS_REPORTED','MATERIAL_TALLY_DETAIL','DEPS_REPACKAGED','EXCESS_UNCONNECTED_BOX') NOT NULL,
  `document_url` varchar(255) NOT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` bigint(20) NOT NULL,
  `deleted_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `dd_deps_id` (`deps_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `deps_line_item_detail`;
CREATE TABLE `deps_line_item_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deps_id` bigint(20) NOT NULL,
  `inbound_deps_record_id` bigint(20) NOT NULL,
  `item_name` varchar(255) NOT NULL,
  `item_value` decimal(10,0) NOT NULL,
  `item_count` int(11) NOT NULL,
  `invoice_value` decimal(10,0) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` bigint(20) NOT NULL,
  `deleted_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `dlid_deps_id` (`deps_id`),
  KEY `dlid_inbound_deps_record_id` (`inbound_deps_record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `deps_record`;
CREATE TABLE `deps_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `inbound_location_id` bigint(20) NOT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  `barcode` varchar(255) DEFAULT NULL,
  `cnote` varchar(255) DEFAULT NULL,
  `box_id` bigint(20) DEFAULT NULL,
  `deps_status` enum('OPEN','ACTION_TAKEN','BAD_POD_SUBMITTED','CLOSED') NOT NULL,
  `deps_type` enum('DAMAGE','PILFERAGE','SHORTAGE','EXCESS','WRONG_SCAN','UNCONNECTED') NOT NULL,
  `initial_deps_type` enum('DAMAGE','PILFERAGE','SHORTAGE','EXCESS','WRONG_SCAN','UNCONNECTED') NOT NULL,
  `unconnected_box_status` enum('UNCONNECTED') DEFAULT NULL,
  `trip_type` enum('BP','FEEDER','LINEHAUL','DRS','PRS','PICKUP','TRIP','DHO') DEFAULT NULL,
  `trip_id` bigint(20) DEFAULT NULL,
  `consignment_id` bigint(20) DEFAULT NULL,
  `reported_by_id` bigint(20) NOT NULL,
  `incharge_id` bigint(20) NOT NULL,
  `incharge_name` varchar(40) DEFAULT NULL,
  `reported_by_name` varchar(40) DEFAULT NULL,
  `vehicle_number` varchar(40) DEFAULT NULL,
  `task_id` bigint(20) DEFAULT NULL,
  `new_barcode` varchar(255) DEFAULT NULL,
  `new_cnote` varchar(255) DEFAULT NULL,
  `resolving_deps_id` bigint(20) DEFAULT NULL,
  `deps_pilferage_status` enum('RESOLVED_WITH_EXCESS') DEFAULT NULL,
  `deps_task_type` enum('LOADING','UNLOADING','STOCK_CHECK','RETURN_SCAN','HANDOVER') DEFAULT NULL,
  `consignment_status` varchar(45) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` bigint(20) NOT NULL,
  `deleted_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `parent_deps_id` bigint(20) DEFAULT NULL,
  `organization_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `dr_inbound_location_id` (`inbound_location_id`),
  KEY `dr_client_id` (`client_id`),
  KEY `dr_cnote` (`cnote`),
  KEY `dr_barcode` (`barcode`),
  KEY `dr_new_cnote` (`new_cnote`),
  KEY `dr_created_at` (`created_at`),
  KEY `dr_is_active` (`is_active`),
  KEY `dr_deps_task_type` (`deps_task_type`),
  KEY `dr_task_id` (`task_id`),
  KEY `dr_box_id` (`box_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `deps_record_reason`;
CREATE TABLE `deps_record_reason` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deps_id` bigint(20) NOT NULL,
  `outbound_deps_id` bigint(20) NOT NULL,
  `outbound_deps_reason` enum('POOR_PACKING','MOVEMENT_IN_VEHICLE','INCREASED_HANDLING','INCORRECT_HANDLING','INCORRECT_STACKING','STACKING_HEIGHT','RISKY_MATERIAL','VEHICLE_DEFECT') DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` bigint(20) NOT NULL,
  `deleted_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `drr_deps_id` (`deps_id`),
  KEY `drr_outbound_deps_id` (`outbound_deps_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `deps_record_status_timeline`;
CREATE TABLE `deps_record_status_timeline` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deps_id` bigint(20) NOT NULL,
  `deps_status` enum('OPEN','ACTION_TAKEN','BAD_POD_SUBMITTED','CLOSED') NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` bigint(20) NOT NULL,
  `deleted_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `drst_deps_id` (`deps_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `deps_reopen_ticket_record`;
CREATE TABLE `deps_reopen_ticket_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deps_id` bigint(20) NOT NULL,
  `new_deps_id` bigint(20) NOT NULL,
  `comments` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT NULL,
  `deleted_at` bigint(20) DEFAULT NULL,
  `created_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `drtr_id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `dho_schedule`;
CREATE TABLE `dho_schedule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `organization_id` bigint(20) NOT NULL,
  `from_location_id` bigint(20) NOT NULL,
  `to_location_id` bigint(20) NOT NULL,
  `max_weight` double NOT NULL,
  `max_volume` double NOT NULL,
  `scheduled_closing_time` bigint(20) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `days_of_week` varchar(20) DEFAULT '1,1,1,1,1,1,1',
  PRIMARY KEY (`id`),
  KEY `is_active_df_from_location_to_location` (`is_active`,`organization_id`,`from_location_id`,`to_location_id`),
  KEY `fk_organization_dho_schedule` (`organization_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `document_fn_mapping`;
CREATE TABLE `document_fn_mapping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `document_id` bigint(20) DEFAULT NULL,
  `fn_id` bigint(20) DEFAULT NULL,
  `file_type` varchar(255) DEFAULT NULL,
  `is_active` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `dfm_index2` (`fn_id`),
  KEY `dfm_index3` (`document_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `edd_calculation_data`;
CREATE TABLE `edd_calculation_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  `consignment_id` bigint(20) NOT NULL,
  `tat_from_location_id` bigint(20) DEFAULT NULL,
  `tat_to_location_id` bigint(20) DEFAULT NULL,
  `tat_client_id` bigint(20) DEFAULT NULL,
  `tat_zoom_pickup_cutoff_time_millis` bigint(20) NOT NULL,
  `tat_express_pickup_cutoff_time_millis` bigint(20) NOT NULL,
  `tat_zoom_delivery_tat_days` int(11) NOT NULL,
  `tat_express_delivery_tat_days` int(11) NOT NULL,
  `tat_delivery_cutoff_time_millis` bigint(20) NOT NULL,
  `cn_created_at` bigint(20) NOT NULL,
  `cn_booked_at` bigint(20) NOT NULL,
  `device_type` varchar(40) DEFAULT NULL,
  `user_type` varchar(255) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `creation_location_type` varchar(45) DEFAULT NULL,
  `lat_long_closest_ou_id` bigint(20) DEFAULT NULL,
  `lat_long_closest_ou_distance` varchar(40) DEFAULT NULL,
  `cn_location_id` bigint(20) NOT NULL,
  `location_dropoff_cut_off` bigint(20) NOT NULL,
  `from_pincode_service_type` varchar(40) NOT NULL,
  `to_pincode_service_type` varchar(40) NOT NULL,
  `sunday_delivery_cpd_increase_flag` tinyint(1) NOT NULL DEFAULT '0',
  `manual_change_remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `consignment_id_unique` (`consignment_id`),
  KEY `ecd_consignment_id` (`consignment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `email_dl`;
CREATE TABLE `email_dl` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dl` varchar(255) NOT NULL,
  `spring_profile` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `exclusion_locations`;
CREATE TABLE `exclusion_locations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `from_location_id` bigint(20) DEFAULT NULL,
  `from_location_code` varchar(255) DEFAULT NULL,
  `to_location_id` bigint(20) DEFAULT NULL,
  `to_location_code` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `feeder_vendor`;
CREATE TABLE `feeder_vendor` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `vendor_name` varchar(255) DEFAULT NULL,
  `vendor_code` varchar(45) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `person_name` varchar(255) DEFAULT NULL,
  `contact_number` bigint(20) DEFAULT NULL,
  `cluster_id` bigint(20) NOT NULL,
  `vendor_type` varchar(255) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKglwm3f5u6ypnmtu1byllp7csv` (`created_by_id`),
  KEY `FKmybhkg5yp9wooi1aqf4q1glfv` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `feeder_vendor_agent`;
CREATE TABLE `feeder_vendor_agent` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `address` varchar(1024) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `license_expiry_date` bigint(20) DEFAULT NULL,
  `license_number` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `vendor_code` varchar(255) DEFAULT NULL,
  `feeder_vendor_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4wckyua9m3o35mu2kq2o6p3l1` (`feeder_vendor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `feeder_vendor_agent_zone`;
CREATE TABLE `feeder_vendor_agent_zone` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `feeder_vendor_agent_id` bigint(20) DEFAULT NULL,
  `zone_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKno8rqvctknvvobn9siyrjywqw` (`feeder_vendor_agent_id`),
  KEY `FK6u7neg4h1vwnusa2vc54b2sco` (`zone_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `feeder_vendor_vehicle`;
CREATE TABLE `feeder_vendor_vehicle` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `vehicle_type` varchar(255) DEFAULT NULL,
  `vehicle_number` varchar(255) DEFAULT NULL,
  `feeder_vendor_id` int(11) NOT NULL,
  `created_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_feeder_vendor_vehicle_1_idx` (`created_by_id`),
  KEY `fk_feeder_vendor_vehiclel_2_idx` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `fn_document`;
CREATE TABLE `fn_document` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `document_id` bigint(20) DEFAULT NULL,
  `consignment_id` bigint(20) DEFAULT NULL,
  `file_type` varchar(255) DEFAULT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `barcode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fd_index2` (`consignment_id`,`file_type`),
  KEY `fd_index3` (`location_id`),
  KEY `fd_index4` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `fn_document_history`;
CREATE TABLE `fn_document_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fn_document_id` bigint(20) DEFAULT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `created_at` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `forward_note`;
CREATE TABLE `forward_note` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `barcode` varchar(45) DEFAULT NULL,
  `awb_number` varchar(45) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `from_location_id` bigint(20) DEFAULT NULL,
  `from_location_type` varchar(255) DEFAULT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  `to_location_id` bigint(20) DEFAULT NULL,
  `to_location_type` varchar(255) DEFAULT NULL,
  `cost` bigint(20) DEFAULT NULL,
  `provider` varchar(255) DEFAULT NULL,
  `is_active` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `fn_index2` (`from_location_id`),
  KEY `fn_index3` (`location_id`),
  KEY `fn_index4` (`to_location_id`,`to_location_type`),
  KEY `fn_index5` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `forward_note_history`;
CREATE TABLE `forward_note_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `forward_note_id` bigint(20) DEFAULT NULL,
  `awb_number` varchar(45) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `created_at` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `function_execution`;
CREATE TABLE `function_execution` (
  `function_name` varchar(255) NOT NULL,
  `is_running` tinyint(4) NOT NULL DEFAULT '0',
  `created_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`function_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `function_execution_history`;
CREATE TABLE `function_execution_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `function_name` varchar(255) DEFAULT NULL,
  `started_at` bigint(20) DEFAULT NULL,
  `finished_at` bigint(20) DEFAULT NULL,
  `exception_occured` tinyint(4) DEFAULT '0',
  `created_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `goods_category`;
CREATE TABLE `goods_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category` varchar(255) DEFAULT NULL,
  `created_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `holiday`;
CREATE TABLE `holiday` (
  `id` bigint(20) NOT NULL,
  `created_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `date` bigint(20) DEFAULT NULL,
  `holiday_name` varchar(45) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `inbound_deps_record`;
CREATE TABLE `inbound_deps_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deps_id` bigint(20) NOT NULL,
  `inbound_location_id` bigint(20) NOT NULL,
  `damage_content_status` enum('MATERIAL_INTACT','MATERIAL_DAMAGED','MATERIAL_MISSING') DEFAULT NULL,
  `damage_extent_type` enum('NOT_TORN','TORN') DEFAULT NULL,
  `shortage_package_status` enum('PACKAGE_FOUND','PACKAGE_NOT_FOUND','PACKAGE_NOT_FOUND_MARKED_BY_OUTBOUND','PACKAGE_FOUND_AT_DIFFERENT_DESTINATION','PACKAGE_FOUND_MARKED_BY_OUTBOUND','PACKAGE_NOT_FOUND_MARKED_AUTOMATICALLY','PACKAGE_FOUND_AND_CONNECTED_BY_SCAN_APP') DEFAULT NULL,
  `deps_status` enum('OPEN','ACTION_TAKEN','BAD_POD_SUBMITTED','CLOSED') DEFAULT NULL,
  `packaged_by` varchar(40) DEFAULT NULL,
  `package_material` text,
  `damage_repackage_reason` enum('LEAKAGE','PACKING_QUALITY_NOT_GOOD','PACKAGE_NOT_TRANSIT_WORTHY') DEFAULT NULL,
  `not_torn_packaging_detail` enum('NOT_REPACKAGED','REPACKAGED') DEFAULT NULL,
  `comments` text,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` bigint(20) NOT NULL,
  `deleted_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idr_deps_id` (`deps_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `industry_type`;
CREATE TABLE `industry_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `content_types` varchar(1024) DEFAULT NULL,
  `cft_value` double NOT NULL DEFAULT '6',
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrehnjl578b6kgsxu9kck7xc4j` (`created_by_id`),
  KEY `FKed08naustglp9gwhtprkbu8uu` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `inventory_tracking_detail`;
CREATE TABLE `inventory_tracking_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `trip_id` bigint(20) NOT NULL,
  `trip_type` varchar(45) DEFAULT NULL,
  `origin_object_id` varchar(45) DEFAULT NULL,
  `object_type` varchar(45) DEFAULT NULL,
  `object_id` varchar(45) DEFAULT NULL,
  `location_id` bigint(20) NOT NULL,
  `origin_availability` tinyint(4) DEFAULT '1',
  `available` tinyint(4) NOT NULL DEFAULT '1',
  `reason` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK83wedmllaxtb2nbfperwvps9yox1252l` (`created_by_id`),
  KEY `FKl89pvwqdnv3ap0f85qmwcsdc3m712312kqu` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `invoice`;
CREATE TABLE `invoice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `consignment_id` bigint(20) DEFAULT NULL,
  `invoice_number` varchar(255) DEFAULT NULL,
  `package_count` int(11) DEFAULT NULL,
  `value` double DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `invoice_date` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4xtnn94t7qv45p5yxj9hvk7mr` (`consignment_id`),
  KEY `i_index3` (`created_by_id`),
  KEY `i_index4` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `linehaul_adhoc_request`;
CREATE TABLE `linehaul_adhoc_request` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `route_id` bigint(20) NOT NULL,
  `vehicle_type` varchar(255) NOT NULL,
  `placement_time` bigint(20) NOT NULL,
  `dispatch_time` bigint(20) NOT NULL,
  `request_status` varchar(255) NOT NULL DEFAULT 'PENDING',
  `approved_at` bigint(20) DEFAULT NULL,
  `approved_by_id` bigint(20) DEFAULT NULL,
  `trip_created` tinyint(1) NOT NULL DEFAULT '0',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `deleted_at` bigint(20) DEFAULT NULL,
  `deleted_by_id` bigint(20) DEFAULT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_linehaul_adhoc_request_route_id` (`route_id`),
  KEY `lar_index3` (`created_by_id`),
  KEY `lar_index4` (`last_updated_by_id`),
  KEY `lar_index5` (`deleted_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `linehaul_schedule`;
CREATE TABLE `linehaul_schedule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `route_id` bigint(20) NOT NULL,
  `vehicle_type` varchar(255) NOT NULL,
  `placement_time` bigint(20) NOT NULL DEFAULT '0',
  `dispatch_time` bigint(20) NOT NULL DEFAULT '0',
  `frequency_type` varchar(255) NOT NULL DEFAULT 'DAILY',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `deleted_at` bigint(20) DEFAULT NULL,
  `deleted_by_id` bigint(20) DEFAULT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `prime_schedule_id` bigint(20) DEFAULT NULL,
  `generation_source_type` varchar(45) DEFAULT NULL,
  `feeder_vendor_id` bigint(20) DEFAULT NULL,
  `feeder_vendor_code` varchar(255) DEFAULT NULL,
  `days_of_week` varchar(255) NOT NULL DEFAULT '1,1,1,1,1,1,1',
  PRIMARY KEY (`id`),
  KEY `fk_linehaul_schedule_route_id` (`route_id`),
  KEY `fk_ls_feeder_vendor_id` (`feeder_vendor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `mall_detail`;
CREATE TABLE `mall_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `address` varchar(1023) NOT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  `pincode` varchar(255) DEFAULT NULL,
  `is_active` tinyint(4) DEFAULT '0',
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqljept46mixryiouf3212r3ht0a` (`created_by_id`),
  KEY `FKfcpe5capw9jfeupgkj9345bgh5iu` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `manifest`;
CREATE TABLE `manifest` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `from_location_id` bigint(20) DEFAULT NULL,
  `mf_name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `to_location_id` bigint(20) DEFAULT NULL,
  `boxes` int(11) DEFAULT NULL,
  `volume` double DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `trip_id` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `cargonet_barcode` varchar(45) DEFAULT NULL,
  `cargonet_status` varchar(45) DEFAULT NULL,
  `cargonet_unavailable_reason_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKoik67u4uhkh158dlp229f0bndtrip` (`trip_id`),
  KEY `m_index5` (`created_by_id`),
  KEY `m_index6` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `manifest_history`;
CREATE TABLE `manifest_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `from_location_id` bigint(20) DEFAULT NULL,
  `manifest_id` bigint(20) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `to_location_id` bigint(20) DEFAULT NULL,
  `trip_id` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `mh_index2` (`created_by_id`),
  KEY `mh_index3` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `oa_task_assignment`;
CREATE TABLE `oa_task_assignment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `trip_id` bigint(20) DEFAULT NULL,
  `trip_type` varchar(255) DEFAULT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  `task_type` varchar(255) NOT NULL,
  `oa_user_id` bigint(20) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `assigned_at` bigint(20) DEFAULT NULL,
  `assigned_by_id` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `dock_number` varchar(45) DEFAULT NULL,
  `scheduled_at` bigint(20) DEFAULT NULL,
  `scheduled_end_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `trip_id_trip_type_location_id_task_type` (`trip_id`,`trip_type`,`location_id`,`task_type`),
  KEY `ota_status_index` (`status`),
  KEY `ota_assigned_at_index` (`assigned_at`),
  KEY `ota_idx_oa_task_assignment_trip_id_trip_type` (`trip_id`,`trip_type`),
  KEY `ota_idx_oa_task_assignment_location_id` (`location_id`),
  KEY `ota_index6` (`created_by_id`),
  KEY `ota_index7` (`last_updated_by_id`),
  KEY `ota_index8` (`user_id`),
  KEY `ota_index9` (`assigned_by_id`),
  KEY `ota_trip_id_trip_type_location_id` (`trip_id`,`trip_type`,`location_id`),
  KEY `ota_user_id` (`user_id`),
  KEY `ota_task_type` (`task_type`),
  KEY `ota_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `organization`;
CREATE TABLE `organization` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `organization_code` varchar(255) NOT NULL,
  `organization_type` enum('RIVIGO','BF','BP','DF') DEFAULT NULL,
  `charge_basis` varchar(45) DEFAULT NULL,
  `factor` double DEFAULT NULL,
  `status` enum('ACTIVE','INACTIVE') DEFAULT NULL,
  `created_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `use_actual_weight` tinyint(4) NOT NULL DEFAULT '0',
  `insurance_applicable` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `organization_code` (`organization_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `outbound_deps_rca_record`;
CREATE TABLE `outbound_deps_rca_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deps_id` bigint(20) NOT NULL,
  `outbound_deps_id` bigint(20) NOT NULL,
  `reason1` text,
  `reason2` text,
  `reason3` text,
  `reason4` text,
  `reason5` text,
  `actions` text,
  `responsibility` text,
  `applicability` text,
  `rca_done_at` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` bigint(20) NOT NULL,
  `deleted_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `odrr_deps_id` (`deps_id`),
  KEY `odrr_outbound_deps_id` (`outbound_deps_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `outbound_deps_record`;
CREATE TABLE `outbound_deps_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deps_id` bigint(20) NOT NULL,
  `outbound_location_id` bigint(20) NOT NULL,
  `outbound_deps_status` enum('OPEN','INVALID','BAD_POD_SUBMITTED','RCA','CLOSED') NOT NULL,
  `deps_type` enum('DAMAGE','PILFERAGE','SHORTAGE','EXCESS','WRONG_SCAN','UNCONNECTED') NOT NULL,
  `shortage_package_status` enum('PACKAGE_FOUND','PACKAGE_NOT_FOUND','PACKAGE_NOT_FOUND_MARKED_AUTOMATICALLY') DEFAULT NULL,
  `reported_to_id` bigint(20) NOT NULL,
  `verify_incharge_id` bigint(20) NOT NULL,
  `situation` text,
  `complication` text,
  `resolution` text,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` bigint(20) NOT NULL,
  `deleted_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `odr_deps_id` (`deps_id`),
  KEY `odr_outbound_location_id` (`outbound_location_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `outbound_deps_record_status_timeline`;
CREATE TABLE `outbound_deps_record_status_timeline` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deps_id` bigint(20) NOT NULL,
  `outbound_deps_id` bigint(20) NOT NULL,
  `outbound_deps_status` enum('OPEN','INVALID','BAD_POD_SUBMITTED','RCA','CLOSED') NOT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` bigint(20) NOT NULL,
  `deleted_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `odrst_deps_id` (`deps_id`),
  KEY `odrst_outbound_deps_id` (`outbound_deps_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `ou_drs_details`;
CREATE TABLE `ou_drs_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `location_id` bigint(20) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `blocked_by_id` bigint(20) DEFAULT NULL,
  `blocked_at` bigint(20) DEFAULT NULL,
  `created_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `odd_location_id` (`location_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `packing_type`;
CREATE TABLE `packing_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `packing` varchar(255) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `payment_collection_task_assignment`;
CREATE TABLE `payment_collection_task_assignment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `consignment_id` bigint(20) DEFAULT NULL,
  `cnote` varchar(45) DEFAULT NULL,
  `actual_payable_amount` bigint(20) DEFAULT NULL,
  `partner_type` varchar(45) DEFAULT NULL,
  `bp_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  `assigned_at` bigint(20) DEFAULT NULL,
  `assigned_by_id` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `payment_details`;
CREATE TABLE `payment_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `consignment_id` bigint(20) NOT NULL,
  `cnote` varchar(255) DEFAULT NULL,
  `payment_type` varchar(255) DEFAULT NULL,
  `payment_mode` varchar(255) DEFAULT NULL,
  `ifsc_code` varchar(255) DEFAULT NULL,
  `cheque_or_draft_number` varchar(255) DEFAULT NULL,
  `charge_basis` varchar(255) DEFAULT NULL,
  `per_unit_cost` double DEFAULT '0',
  `base_freight` double DEFAULT '0',
  `docket_charges` double DEFAULT '0',
  `fuel_surcharge_percent` double DEFAULT '0',
  `fuel_surcharges` double DEFAULT '0',
  `rov_charge_percent` double DEFAULT '0',
  `rov_charges` double DEFAULT '0',
  `oda_charges` double DEFAULT '0',
  `handling_charges` double DEFAULT '0',
  `vas_charges` double DEFAULT '0',
  `other_charges` double DEFAULT '0',
  `service_tax_percent` double DEFAULT '0',
  `uploaded_file_id` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `conversion_factor` double DEFAULT '0',
  `active` tinyint(1) DEFAULT '1',
  `actual_payable_amount` double DEFAULT '0',
  `payment_status` varchar(45) DEFAULT NULL,
  `cashbook_consignment_status` varchar(50) DEFAULT 'PENDING',
  `cashbook_collection_id` bigint(20) DEFAULT NULL,
  `cashbook_deposit_id` bigint(20) DEFAULT NULL,
  `cashbook_agreed_amount` double DEFAULT NULL,
  `tds_amount` double DEFAULT NULL,
  `cashbook_last_action_by_id` bigint(20) DEFAULT NULL,
  `cashbook_reason` varchar(255) DEFAULT NULL,
  `remittance_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pd_consignment_id` (`consignment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `payment_details_history`;
CREATE TABLE `payment_details_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `consignment_id` bigint(20) NOT NULL,
  `payment_details_id` bigint(20) NOT NULL,
  `actual_payable_amount` double DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  `payment_status` varchar(45) DEFAULT NULL,
  `cashbook_consignment_status` varchar(50) DEFAULT NULL,
  `cashbook_collection_id` bigint(20) DEFAULT NULL,
  `cashbook_agreed_amount` double DEFAULT NULL,
  `cashbook_last_action_by_id` bigint(20) DEFAULT NULL,
  `cashbook_deposit_id` bigint(20) DEFAULT NULL,
  `cashbook_reason` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `payment_detail_master`;
CREATE TABLE `payment_detail_master` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `from_pincode` varchar(255) DEFAULT NULL,
  `to_pincode` varchar(255) DEFAULT NULL,
  `consignee_phone` varchar(255) DEFAULT NULL,
  `charge_basis` varchar(255) DEFAULT NULL,
  `per_unit_cost` double DEFAULT '0',
  `base_freight` double DEFAULT '0',
  `docket_charges` double DEFAULT '0',
  `fuel_surcharge_percent` double DEFAULT '0',
  `fuel_surcharges` double DEFAULT '0',
  `rov_charge_percent` double DEFAULT '0',
  `rov_charges` double DEFAULT '0',
  `oda_charges` double DEFAULT '0',
  `handling_charges` double DEFAULT '0',
  `vas_charges` double DEFAULT '0',
  `other_charges` double DEFAULT '0',
  `service_tax_percent` double DEFAULT '0',
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `conversion_factor` double DEFAULT '0',
  `active` tinyint(1) DEFAULT '1',
  `address` varchar(255) DEFAULT NULL,
  `consignee_name` varchar(255) DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `volume` double DEFAULT NULL,
  `number_of_boxes` bigint(20) DEFAULT NULL,
  `pickup_id` bigint(20) DEFAULT NULL,
  `payment_mode` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `paytm_payment_details`;
CREATE TABLE `paytm_payment_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `consignment_id` bigint(20) NOT NULL,
  `cnote` varchar(255) DEFAULT NULL,
  `actual_payable_amount` double DEFAULT '0',
  `payment_status` varchar(45) DEFAULT NULL,
  `payTM_response` varchar(5000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `paytm_qr_code`;
CREATE TABLE `paytm_qr_code` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `qrCode` varchar(1025) NOT NULL,
  `consignmentId` bigint(20) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `pickup`;
CREATE TABLE `pickup` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `number_of_boxes` int(11) DEFAULT NULL,
  `pickup_date_time` bigint(20) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `volume` double DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `business_partner_id` bigint(20) DEFAULT NULL,
  `client_address_id` bigint(20) DEFAULT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  `remarks` varchar(3500) DEFAULT NULL,
  `scheduled_consignment_id` bigint(20) DEFAULT NULL,
  `display_name` varchar(255) DEFAULT NULL,
  `detailed_address` varchar(1024) DEFAULT NULL,
  `pincode` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `mobile_no` varchar(255) DEFAULT NULL,
  `client_code` varchar(255) DEFAULT NULL,
  `rescheduled_date_time` bigint(20) DEFAULT NULL,
  `failure_reason_id` bigint(20) DEFAULT NULL,
  `duplicate_prq_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `prs_id` bigint(10) DEFAULT NULL,
  `contact_person` varchar(255) DEFAULT NULL,
  `contact_number` varchar(45) DEFAULT NULL,
  `contact_email` varchar(45) DEFAULT NULL,
  `days_of_week` varchar(45) DEFAULT NULL,
  `is_recurring` tinyint(1) DEFAULT NULL,
  `report_reason` varchar(255) DEFAULT NULL,
  `report_status` varchar(45) DEFAULT NULL,
  `cut_off_time` bigint(20) DEFAULT NULL,
  `old_prq_id` bigint(20) DEFAULT NULL,
  `latitude_captured_on_cwh_reach` double DEFAULT NULL,
  `lonitude_captured_on_cwh_reach` double DEFAULT NULL,
  `payment_mode` varchar(45) DEFAULT NULL,
  `is_drop` tinyint(4) DEFAULT '0',
  `pickup_time_slot` varchar(45) DEFAULT NULL,
  `weight_range` varchar(45) DEFAULT NULL,
  `vehicle_number` varchar(45) DEFAULT NULL,
  `vehicle_type` varchar(45) DEFAULT NULL,
  `vendor_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqahxqvoglyisttd4te39t6bq5` (`business_partner_id`),
  KEY `FKhmpsp0ihiexhc7hxi8iq0137u` (`client_address_id`),
  KEY `fk_pickup_3_idx` (`failure_reason_id`),
  KEY `p_user_id` (`user_id`),
  KEY `p_index6` (`created_by_id`),
  KEY `p_index7` (`last_updated_by_id`),
  KEY `p_prs_id` (`prs_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `pickup_failure_reason`;
CREATE TABLE `pickup_failure_reason` (
  `id` bigint(20) NOT NULL,
  `reason` varchar(255) NOT NULL,
  `user_type` varchar(255) NOT NULL,
  `reschedule_allowed` tinyint(4) NOT NULL,
  `pickup_id_required` tinyint(4) NOT NULL DEFAULT '0',
  `reach_status` varchar(255) DEFAULT NULL,
  `pickup_failure_status` varchar(255) NOT NULL,
  `client_facing_reason` varchar(255) DEFAULT NULL,
  `client_facing_remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `pickup_history`;
CREATE TABLE `pickup_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `pickup_id` bigint(20) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `device_id_type` varchar(45) DEFAULT NULL,
  `device_id` varchar(255) DEFAULT NULL,
  `app_version` varchar(255) DEFAULT NULL,
  `device_type` varchar(45) DEFAULT NULL,
  `bp_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `failure_reason_id` bigint(20) DEFAULT NULL,
  `pickup_date` bigint(20) DEFAULT NULL,
  `pickup_time_slot` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_pickup_history_pickup_id` (`pickup_id`),
  KEY `ph_index3` (`created_by_id`),
  KEY `ph_index4` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `pickup_run_sheet`;
CREATE TABLE `pickup_run_sheet` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `bp_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `oa_id` bigint(20) DEFAULT NULL,
  `assignor_id` bigint(20) DEFAULT NULL,
  `vehicle_number` varchar(45) DEFAULT NULL,
  `vendor_name` varchar(255) DEFAULT NULL,
  `cash_closure` bigint(20) DEFAULT NULL,
  `oa_user_id` bigint(20) DEFAULT NULL,
  `dock_number` varchar(45) DEFAULT NULL,
  `vehicle_type` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqahxqvoglyisttd4te39t6bq5123` (`bp_id`),
  KEY `prs_index4` (`created_by_id`),
  KEY `prs_index5` (`last_updated_by_id`),
  KEY `prs_index6` (`user_id`),
  KEY `prs_index7` (`oa_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `pincode`;
CREATE TABLE `pincode` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `code` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `pickup_zone_id` bigint(20) DEFAULT NULL,
  `delivery_zone_id` bigint(20) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `pincode_service_type` varchar(45) NOT NULL DEFAULT 'SERVICEABLE',
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `distance_to_pickup_branch` bigint(20) DEFAULT NULL,
  `distance_to_pickup_pc` bigint(20) DEFAULT NULL,
  `distance_to_delivery_branch` bigint(20) DEFAULT NULL,
  `distance_to_delivery_pc` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code_UNIQUE` (`code`),
  KEY `FK83wedmllaxtb2nbfpvps9yoxl` (`created_by_id`),
  KEY `FKl89pvwqdnv3ap0f85qmwcmkqu` (`last_updated_by_id`),
  KEY `FKby9btadipvtxjksfb7xykxs0p` (`pickup_zone_id`),
  KEY `p_delivery_zone_id` (`delivery_zone_id`),
  KEY `p_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `pincodegeo`;
CREATE TABLE `pincodegeo` (
  `pincode` varchar(255) DEFAULT NULL,
  `place` text,
  `name1` text,
  `code1` int(11) DEFAULT NULL,
  `name2` text,
  `code2` int(11) DEFAULT NULL,
  `name3` text,
  `code3` text,
  `lat` double DEFAULT NULL,
  `lon` double DEFAULT NULL,
  `accuracy` int(11) DEFAULT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `pincode_service_type`;
CREATE TABLE `pincode_service_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `extra_charge_factor` double NOT NULL,
  `extra_edd_in_days` int(11) NOT NULL,
  `type` varchar(40) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `extra_charge_factor_extra_edd_in_days` (`extra_charge_factor`,`extra_edd_in_days`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `prime_vehicle_rate_master`;
CREATE TABLE `prime_vehicle_rate_master` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ft32_cost` double NOT NULL,
  `ft22_cost` double NOT NULL,
  `createdAt` bigint(20) NOT NULL,
  `lastUpdatedAt` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `qc_details`;
CREATE TABLE `qc_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `consignment_id` bigint(20) NOT NULL,
  `cnote` varchar(45) DEFAULT NULL,
  `location_id` bigint(20) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `qd_index2` (`created_by_id`),
  KEY `qd_index3` (`last_updated_by_id`),
  KEY `qd_consignment_id` (`consignment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `qc_invoice_details`;
CREATE TABLE `qc_invoice_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `consignment_id` bigint(20) DEFAULT NULL,
  `invoice_id` bigint(20) DEFAULT NULL,
  `invoice_no` varchar(255) DEFAULT NULL,
  `value` double DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `qid_consignment_id` (`consignment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `qc_volume_details`;
CREATE TABLE `qc_volume_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `breadth` double DEFAULT NULL,
  `consignment_id` bigint(20) DEFAULT NULL,
  `height` double DEFAULT NULL,
  `length` double DEFAULT NULL,
  `number_of_boxes` int(11) DEFAULT NULL,
  `unit` varchar(255) DEFAULT NULL,
  `volume` double DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmtwe7i2an0dv8gxasndjaesovb` (`consignment_id`),
  KEY `qvd_index3` (`created_by_id`),
  KEY `qvd_index4` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `reason`;
CREATE TABLE `reason` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `reason` varchar(255) NOT NULL,
  `sub_reason` varchar(255) DEFAULT NULL,
  `client_facing_reason` varchar(255) DEFAULT NULL,
  `client_facing_sub_reason` varchar(255) DEFAULT NULL,
  `snooze_category` varchar(255) DEFAULT NULL,
  `reschedule_allowed` tinyint(1) NOT NULL DEFAULT '0',
  `contact_details_required` tinyint(1) NOT NULL DEFAULT '0',
  `appointment_required` tinyint(1) NOT NULL DEFAULT '0',
  `vehicle_dispatch_alerts` tinyint(1) NOT NULL DEFAULT '0',
  `cn_dispatch_alerts` tinyint(1) NOT NULL DEFAULT '0',
  `cn_delivery_alerts` tinyint(1) NOT NULL DEFAULT '0',
  `cn_other_alerts` tinyint(1) NOT NULL DEFAULT '0',
  `dispatch_hold` tinyint(1) NOT NULL DEFAULT '0',
  `trip_recommendation` tinyint(1) NOT NULL DEFAULT '0',
  `delivery_hold` tinyint(1) NOT NULL DEFAULT '0',
  `delivery_failure` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `pickup_unassigned` tinyint(1) NOT NULL DEFAULT '0',
  `pickup_delayed` tinyint(1) NOT NULL DEFAULT '0',
  `deps_stockcheck` tinyint(1) NOT NULL DEFAULT '0',
  `cod_dod` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `reason_sub_reason` (`reason`,`sub_reason`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `regional_hop_distance`;
CREATE TABLE `regional_hop_distance` (
  `Id` int(11) DEFAULT NULL,
  `route` text,
  `start_location` text,
  `end_location` text,
  `concat` text,
  `km` int(11) DEFAULT NULL,
  `total_km` int(11) DEFAULT NULL,
  `route_type` text,
  `capacity` int(11) DEFAULT NULL,
  `cost_single_side` int(11) DEFAULT NULL,
  `sn` int(11) DEFAULT NULL,
  `vendor` text,
  `vehicle_type` text,
  `capacity_qntl` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `regional_linehaul_cost`;
CREATE TABLE `regional_linehaul_cost` (
  `id` int(11) DEFAULT NULL,
  `route` text,
  `route_name` text,
  `vendor_name` text,
  `route_code` text,
  `vendor_code` text,
  `zone` text,
  `google_km` int(11) DEFAULT NULL,
  `vehicle_type` int(11) DEFAULT NULL,
  `cost_with_toll` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `replaced_barcode_mapping`;
CREATE TABLE `replaced_barcode_mapping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `barcode` varchar(40) NOT NULL,
  `cnote` varchar(20) NOT NULL,
  `old_barcode` varchar(40) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` bigint(20) NOT NULL,
  `deleted_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `rbm_barcode` (`barcode`),
  KEY `rbm_old_barcode` (`old_barcode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `report`;
CREATE TABLE `report` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category` varchar(255) DEFAULT NULL,
  `reportType` int(11) DEFAULT '0',
  `emails` varchar(2048) DEFAULT NULL,
  `owners` varchar(2048) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `schedule` varchar(255) DEFAULT NULL,
  `logic_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `next_execution_at` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_3ktdukpv05gk1v1wwbk5rfuel` (`logic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `report_logic`;
CREATE TABLE `report_logic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `logic` mediumtext,
  `logic_type` int(11) DEFAULT NULL,
  `rest_method` int(11) DEFAULT '0',
  `rest_body` varchar(1500) DEFAULT NULL,
  `variables` varchar(255) DEFAULT NULL,
  `collection` varchar(255) DEFAULT NULL,
  `fields` varchar(1023) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `retail_payment_master`;
CREATE TABLE `retail_payment_master` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `from_location` varchar(45) DEFAULT NULL,
  `to_location` varchar(45) DEFAULT NULL,
  `charge_basis` varchar(255) DEFAULT NULL,
  `per_unit_cost` double DEFAULT '0',
  `base_freight` double DEFAULT '0',
  `docket_charges` double DEFAULT '0',
  `fuel_surcharge_percent` double DEFAULT '0',
  `fuel_surcharges` double DEFAULT '0',
  `rov_charge_percent` double DEFAULT '0',
  `rov_charges` double DEFAULT '0',
  `oda_charges` double DEFAULT '0',
  `handling_charges` double DEFAULT '0',
  `vas_charges` double DEFAULT '0',
  `other_charges` double DEFAULT '0',
  `service_tax_percent` double DEFAULT '0',
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `conversion_factor` double DEFAULT '0',
  `active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `route`;
CREATE TABLE `route` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `category` varchar(255) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `end_location_id` bigint(20) DEFAULT NULL,
  `start_location_id` bigint(20) DEFAULT NULL,
  `tat` decimal(10,2) DEFAULT NULL,
  `type` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_9n4b1gwpv0dl4q7cdhgee9rp` (`code`),
  KEY `FKopqkrwk9rnr2yjdm1r9badcme` (`created_by_id`),
  KEY `FKdovxpcpbfuyjo690w1f9qqpr0` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `route_via_location`;
CREATE TABLE `route_via_location` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `route_id` bigint(20) DEFAULT NULL,
  `stop_sequence` int(11) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `via_location_id` bigint(20) DEFAULT NULL,
  `tat` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `FKa8eteq9h2ubt8yyq6v3kw9axk` (`created_by_id`),
  KEY `FKf45xbqk7nf8sd3awmoh9nfwn9` (`last_updated_by_id`),
  KEY `FKbip5c7pq5f78ahhh5g19688hy` (`route_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `scheduled_consignment`;
CREATE TABLE `scheduled_consignment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `from_pincode` int(11) NOT NULL,
  `to_pincode` int(11) NOT NULL,
  `volume` int(11) NOT NULL,
  `weight` int(11) NOT NULL,
  `client_id` bigint(20) NOT NULL,
  `pickup_time` bigint(20) NOT NULL,
  `client_address_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(4) NOT NULL,
  `validity_start` bigint(20) NOT NULL,
  `validity_end` bigint(20) NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  `days_of_week` varchar(255) NOT NULL,
  `is_pickup` tinyint(4) NOT NULL DEFAULT '0',
  `time_to_live_in_minutes` int(11) NOT NULL DEFAULT '180',
  PRIMARY KEY (`id`),
  KEY `sc_client_id` (`client_id`),
  KEY `sc_client_address_id` (`client_address_id`),
  KEY `sc_created_by_id` (`created_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `schedule_task_config`;
CREATE TABLE `schedule_task_config` (
  `task_id` varchar(255) NOT NULL,
  `cron_expression` varchar(255) DEFAULT NULL,
  `last_executed_at` bigint(20) DEFAULT NULL,
  `run_on_api_server` tinyint(1) NOT NULL DEFAULT '0',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `is_running` tinyint(4) NOT NULL DEFAULT '0',
  `next_execution_at` bigint(20) DEFAULT NULL,
  `running_on_instance` varchar(1024) DEFAULT '',
  PRIMARY KEY (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `schedule_task_history`;
CREATE TABLE `schedule_task_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_id` varchar(255) NOT NULL,
  `started_at` bigint(20) DEFAULT NULL,
  `finished_at` bigint(20) DEFAULT NULL,
  `exception_occured` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `running_on_instance` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `shortage_excess_box_record`;
CREATE TABLE `shortage_excess_box_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deps_type` varchar(255) NOT NULL,
  `deps_box_final_status` varchar(255) DEFAULT NULL,
  `barcode` varchar(255) NOT NULL,
  `cnote` varchar(255) DEFAULT NULL,
  `reporting_location_id` bigint(20) NOT NULL,
  `resolved_location_id` bigint(20) DEFAULT NULL,
  `comments` varchar(1024) DEFAULT NULL,
  `resolution_steps` varchar(1024) DEFAULT NULL,
  `consignment_id_new` bigint(20) DEFAULT NULL,
  `resolution_url` varchar(255) DEFAULT NULL,
  `shortage_excess_record_id` bigint(20) DEFAULT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `sebr_id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `shortage_excess_record`;
CREATE TABLE `shortage_excess_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `depsType` varchar(255) NOT NULL,
  `cnote` varchar(255) DEFAULT NULL,
  `no_of_boxes_detected` bigint(20) NOT NULL,
  `no_of_boxes_resolved` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `location_id` bigint(20) NOT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ser_id_UNIQUE` (`id`),
  KEY `fk_shortage_excess_record_1_idx` (`created_by_id`),
  KEY `fk_shortage_excess_record_2_idx` (`last_updated_by_id`),
  KEY `client_id_shortage_fk_idx` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `shortage_excess_record_old`;
CREATE TABLE `shortage_excess_record_old` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `depsType` varchar(255) NOT NULL,
  `consignment_id` bigint(20) DEFAULT NULL,
  `no_of_boxes_detected` bigint(20) NOT NULL,
  `shortage_excess_barcodes` varchar(1024) NOT NULL,
  `comments` varchar(255) DEFAULT NULL,
  `deps_final_status` varchar(255) DEFAULT NULL,
  `resolution_steps` varchar(255) DEFAULT NULL,
  `consignment_id_new` bigint(20) DEFAULT NULL,
  `resolved_barcodes` varchar(255) DEFAULT NULL,
  `no_of_boxes_resolved` bigint(20) DEFAULT NULL,
  `resolution_url` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `location_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKottr7a0co2ea55dkn1msog4ck2` (`created_by_id`),
  KEY `FKmiwr992pm4i9wbqigx7gowgid2` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `stockaccumulator`;
CREATE TABLE `stockaccumulator` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `stock_accumulator_role` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `accumulation_partner_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `availability_status` varchar(255) NOT NULL DEFAULT 'AVAILABLE',
  PRIMARY KEY (`id`),
  UNIQUE KEY `FK9go7oh408aeghrth6lanut9ev` (`user_id`),
  UNIQUE KEY `index6` (`email`),
  KEY `FKa60h9qa8v0imjfjsp8oqj7i5d` (`created_by_id`),
  KEY `FKntgxqfmcihpwtniih6l03fhcl` (`last_updated_by_id`),
  KEY `FK6w94nyh4j78wnbymh8r2xc6ax` (`accumulation_partner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `stockaccumulator_zone`;
CREATE TABLE `stockaccumulator_zone` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `drop_suppport` bit(1) DEFAULT NULL,
  `pickup_suppport` bit(1) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `stockaccumulator_id` bigint(20) DEFAULT NULL,
  `zone_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `FKers1ereldwcnuo6vclrl2g5ar` (`stockaccumulator_id`),
  KEY `FK3d7h9l8ifxj82qco652xm4q0f` (`created_by_id`),
  KEY `FK6lgcrnsmos8f287q5s6seiq46` (`last_updated_by_id`),
  KEY `FK1nyp1qsk5xqvi8vpc7vydrneh` (`zone_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `stockaccumulator_zone_client`;
CREATE TABLE `stockaccumulator_zone_client` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `zone_id` bigint(20) DEFAULT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `zone_client` (`zone_id`,`client_id`),
  KEY `fk_client_idx` (`client_id`),
  KEY `fk_zone_idx` (`zone_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `stock_check`;
CREATE TABLE `stock_check` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `bar_code` varchar(255) DEFAULT NULL,
  `box_id` bigint(20) DEFAULT NULL,
  `consignment_id` bigint(20) DEFAULT NULL,
  `cnote` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  `task_id` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `check_type` varchar(45) DEFAULT NULL,
  `deps_reason_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `sc_task_id` (`task_id`),
  KEY `sc_status` (`status`),
  KEY `sc_check_type` (`check_type`),
  KEY `sc_box_id` (`box_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `task_user`;
CREATE TABLE `task_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `task_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `task_status` varchar(45) DEFAULT NULL,
  `is_active` tinyint(4) NOT NULL DEFAULT '1',
  `deleted_at` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `tracked_node`;
CREATE TABLE `tracked_node` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `node_name` varchar(255) NOT NULL,
  `latitude` double NOT NULL DEFAULT '0',
  `longitude` double NOT NULL DEFAULT '0',
  `radius_KM` double NOT NULL DEFAULT '0.5',
  `location_id` bigint(20) NOT NULL,
  `is_active` tinyint(4) NOT NULL DEFAULT '1',
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `tracked_vehicle`;
CREATE TABLE `tracked_vehicle` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `vehicle_number` varchar(255) NOT NULL,
  `tracking_start_time` bigint(20) NOT NULL,
  `tracking_end_time` bigint(20) NOT NULL,
  `trip_id` bigint(20) NOT NULL,
  `is_active` tinyint(4) NOT NULL DEFAULT '1',
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_trip` (`trip_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `transhipment_drop_suggestion`;
CREATE TABLE `transhipment_drop_suggestion` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `from_location_id` bigint(20) NOT NULL,
  `to_location_id` bigint(20) NOT NULL,
  `suggested_drop_location_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `from_location_id_to_location_id` (`from_location_id`,`to_location_id`),
  KEY `fk_transhipment_drop_suggestion_1_idx` (`from_location_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `transhipment_drop_suggestion_adhoc`;
CREATE TABLE `transhipment_drop_suggestion_adhoc` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `from_location_id` bigint(20) NOT NULL,
  `to_location_id` bigint(20) NOT NULL,
  `suggested_drop_location_id` bigint(20) NOT NULL,
  `expires_at` bigint(20) NOT NULL,
  `deactivated_at` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `transportation_partner_mapping`;
CREATE TABLE `transportation_partner_mapping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `transportation_type` varchar(255) NOT NULL,
  `transportation_id` bigint(20) NOT NULL,
  `partner_type` varchar(255) DEFAULT NULL,
  `partner_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `partner_metadata` varchar(255) DEFAULT NULL,
  `value` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_transportation_partner_mapping_1` (`transportation_type`,`transportation_id`),
  KEY `tpm_transportation_partner_mapping_4_idx` (`partner_type`,`partner_id`),
  KEY `tpm_transportation_partner_mapping_3_idx` (`transportation_id`,`transportation_type`),
  KEY `tpm_index5` (`created_by_id`),
  KEY `tpm_index6` (`last_updated_by_id`),
  KEY `tpm_index7` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `trip`;
CREATE TABLE `trip` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `arrival_time` bigint(20) DEFAULT NULL,
  `scheduled_arrival_time` bigint(20) NOT NULL DEFAULT '631152000000',
  `business_partner_id` bigint(20) DEFAULT NULL,
  `dispatch_time` bigint(20) DEFAULT NULL,
  `instructions` varchar(255) DEFAULT NULL,
  `partner_trip_code` varchar(255) DEFAULT NULL,
  `placement_time` bigint(20) DEFAULT NULL,
  `scheduled_placement_time` bigint(20) NOT NULL DEFAULT '631152000000',
  `route_id` bigint(20) DEFAULT NULL,
  `scheduled_dispatch_time` bigint(20) DEFAULT NULL,
  `seal_number` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `vehicle_number` varchar(255) DEFAULT NULL,
  `vehicle_type` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `generation_source_type` varchar(45) DEFAULT NULL,
  `generation_source_id` bigint(20) DEFAULT NULL,
  `percent_util` int(11) NOT NULL DEFAULT '0',
  `vehicle_location_str` varchar(255) DEFAULT NULL,
  `vehicle_location_update_time` bigint(20) DEFAULT NULL,
  `actual_start_time` bigint(20) DEFAULT '0',
  `actual_end_time` bigint(20) DEFAULT '0',
  `type` varchar(45) DEFAULT NULL,
  `trip_cost` double DEFAULT NULL,
  `feeder_vendor_id` bigint(20) DEFAULT NULL,
  `feeder_vendor_code` varchar(45) DEFAULT NULL,
  `driver_name` varchar(45) DEFAULT NULL,
  `driver_number` varchar(45) DEFAULT NULL,
  `user_id` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqoedvkaixg05cq2errn39sb49` (`business_partner_id`),
  KEY `FKcelj7w6yvj3x9txrqda5k3tai` (`route_id`),
  KEY `t_created_at_index` (`created_at`),
  KEY `t_status_index` (`status`),
  KEY `t_index6` (`created_by_id`),
  KEY `t_index7` (`last_updated_by_id`),
  KEY `t_feeder_vendor_id` (`feeder_vendor_id`),
  KEY `t_id_status` (`id`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `trip_asset`;
CREATE TABLE `trip_asset` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `task_id` bigint(20) NOT NULL,
  `manifest_id` bigint(20) DEFAULT NULL,
  `trip_id` bigint(20) NOT NULL,
  `asset_id` bigint(20) DEFAULT NULL,
  `asset_type` varchar(255) NOT NULL,
  `unavailable_reason_id` bigint(20) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `is_active` tinyint(4) NOT NULL DEFAULT '1',
  `version` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_trip_asset_1_idx` (`task_id`),
  KEY `fk_trip_asset_2_idx` (`manifest_id`),
  KEY `fk_trip_asset_3_idx` (`trip_id`),
  KEY `fk_trip_asset_4_idx` (`asset_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `trip_history`;
CREATE TABLE `trip_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `arrival_time` tinyblob,
  `business_partner_id` bigint(20) DEFAULT NULL,
  `dispatch_time` tinyblob,
  `partner_trip_code` varchar(255) DEFAULT NULL,
  `placement_time` tinyblob,
  `route_id` bigint(20) DEFAULT NULL,
  `seal_number` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `trip_id` bigint(20) DEFAULT NULL,
  `vehicle_number` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `th_index2` (`trip_id`),
  KEY `th_index3` (`created_by_id`),
  KEY `th_index4` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `trip_manifest`;
CREATE TABLE `trip_manifest` (
  `manifest_id` bigint(20) NOT NULL,
  `trip_id` bigint(20) NOT NULL,
  KEY `FKlp1pyga1n04cj88cw6lpps566` (`trip_id`),
  KEY `FKi9exqnpjscl216cub65f3h380` (`manifest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `trip_tracking`;
CREATE TABLE `trip_tracking` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `delay_reason_arrival` varchar(255) DEFAULT NULL,
  `delay_reason_dispatch` varchar(255) DEFAULT NULL,
  `in_time` bigint(20) DEFAULT NULL,
  `original_scheduled_in_time` bigint(20) NOT NULL DEFAULT '631152000000',
  `scheduled_in_time` bigint(20) NOT NULL DEFAULT '631152000000',
  `location_id` bigint(20) DEFAULT NULL,
  `out_time` bigint(20) DEFAULT NULL,
  `original_scheduled_out_time` bigint(20) NOT NULL DEFAULT '631152000000',
  `scheduled_out_time` bigint(20) NOT NULL DEFAULT '631152000000',
  `stop_sequence` int(11) DEFAULT NULL,
  `trip_id` bigint(20) DEFAULT NULL,
  `trip_tracking_status` varchar(255) DEFAULT NULL,
  `seal_number` varchar(255) DEFAULT NULL,
  `wrong_entry_reason` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) NOT NULL DEFAULT '57',
  `last_updated_by_id` bigint(20) NOT NULL DEFAULT '57',
  `gps_in_time` bigint(20) DEFAULT NULL,
  `gps_out_time` bigint(20) DEFAULT NULL,
  `arrived_at` bigint(20) DEFAULT NULL,
  `arrived_by_id` bigint(20) DEFAULT NULL,
  `dispatched_at` bigint(20) DEFAULT NULL,
  `dispatched_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKmgqa1dfqpmxx1th3lhfnjtvf8` (`trip_id`,`location_id`),
  KEY `tt_index4` (`created_by_id`),
  KEY `tt_index5` (`last_updated_by_id`),
  KEY `tt_trip_id` (`trip_id`),
  KEY `tt_location_id` (`location_id`),
  KEY `tt_trip_tracking_status` (`trip_tracking_status`),
  KEY `tt_stop_sequence` (`stop_sequence`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `trip_uploaded_files`;
CREATE TABLE `trip_uploaded_files` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `trip_id` bigint(20) NOT NULL,
  `manifest_id` bigint(20) DEFAULT NULL,
  `task_id` bigint(20) DEFAULT NULL,
  `file_type` varchar(255) DEFAULT NULL,
  `s3URL` varchar(2048) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `task_type` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `turn_around_time`;
CREATE TABLE `turn_around_time` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) NOT NULL,
  `last_updated_by_id` bigint(20) NOT NULL,
  `from_location_id` bigint(20) DEFAULT NULL,
  `from_pincode` varchar(255) DEFAULT NULL,
  `to_location_id` bigint(20) DEFAULT NULL,
  `to_pincode` varchar(255) DEFAULT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  `from_client_address_id` bigint(20) DEFAULT NULL,
  `search_key` varchar(255) DEFAULT NULL,
  `zoom_pickup_cutoff_time_millis` bigint(20) NOT NULL,
  `express_pickup_cutoff_time_millis` bigint(20) NOT NULL,
  `zoom_delivery_tat_days` int(11) NOT NULL,
  `express_delivery_tat_days` int(11) NOT NULL,
  `delivery_cutoff_time_millis` bigint(20) NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tat_index5` (`search_key`),
  UNIQUE KEY `tat_unique_index` (`from_location_id`,`from_pincode`,`to_location_id`,`to_pincode`,`client_id`,`from_client_address_id`),
  KEY `fk_turn_around_time_1_idx` (`created_by_id`),
  KEY `fk_turn_around_time_2_idx` (`last_updated_by_id`),
  KEY `tat_is_active_search_key` (`is_active`,`search_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `unconnected_box_detail`;
CREATE TABLE `unconnected_box_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deps_id` bigint(20) NOT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  `origin_id` bigint(20) DEFAULT NULL,
  `destination_id` bigint(20) DEFAULT NULL,
  `client` varchar(40) DEFAULT NULL,
  `origin` varchar(40) DEFAULT NULL,
  `destination` varchar(40) DEFAULT NULL,
  `cnote` varchar(40) DEFAULT NULL,
  `barcode_digit` varchar(255) DEFAULT NULL,
  `selected_barcode` varchar(255) DEFAULT NULL,
  `client_barcode` varchar(40) DEFAULT NULL,
  `invoice` varchar(40) DEFAULT NULL,
  `remarks` varchar(40) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` bigint(20) NOT NULL,
  `deleted_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ubd_deps_id` (`deps_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `undelivered_consignments`;
CREATE TABLE `undelivered_consignments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `old_drs_id` bigint(20) DEFAULT NULL,
  `new_drs_id` bigint(20) DEFAULT NULL,
  `consignment_id` bigint(20) DEFAULT NULL,
  `cnote` varchar(255) DEFAULT NULL,
  `person_number` bigint(20) DEFAULT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `sub_reason` varchar(255) DEFAULT NULL,
  `person_name` varchar(255) DEFAULT NULL,
  `proposed_delivery_date_time` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `comments` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `uc_consignment_id _index` (`consignment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `mobile_no` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `organization_id` bigint(20) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  KEY `FKottr7a0co2ea55dkn1msog4ck` (`created_by_id`),
  KEY `FKmiwr992pm4i9wbqigx7gowgid` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `user_client_address_history`;
CREATE TABLE `user_client_address_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `entity_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `client_address_id` bigint(20) DEFAULT NULL,
  `address_type` varchar(45) DEFAULT NULL,
  `entity_type` varchar(45) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `created_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id_created_at_entity_type_address_type` (`user_id`,`created_at`,`entity_type`,`address_type`),
  KEY `ucah_user_id` (`user_id`),
  KEY `ucah_created_at` (`created_at`),
  KEY `ucah_entity_type` (`entity_type`),
  KEY `ucah_address_type` (`address_type`),
  KEY `entity_id_entity_type_address_type` (`entity_id`,`entity_type`,`address_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `value_added_services`;
CREATE TABLE `value_added_services` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `consignment_id` bigint(20) DEFAULT NULL,
  `cnote` varchar(255) DEFAULT NULL,
  `mall_delivery` tinyint(1) DEFAULT '0',
  `special_delivery_time` tinyint(1) DEFAULT '0',
  `delivery_from_time_millis` bigint(20) DEFAULT NULL,
  `delivery_to_time_millis` bigint(20) DEFAULT NULL,
  `sunday_holiday_delivery` tinyint(1) DEFAULT '0',
  `active` tinyint(1) DEFAULT '1',
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `pickup_id` bigint(20) DEFAULT NULL,
  `payment_detail_master_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `vas_consignment_id` (`consignment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `vehicle_detail`;
CREATE TABLE `vehicle_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `vehicle_type` varchar(255) DEFAULT NULL,
  `data_type` varchar(255) DEFAULT NULL,
  `data_value` varchar(255) DEFAULT NULL,
  `created_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_vehicle_detail_1_idx` (`created_by_id`),
  KEY `fk_vehicle_detail_2_idx` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `vehicle_pre_inspection`;
CREATE TABLE `vehicle_pre_inspection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trip_type` varchar(40) DEFAULT NULL,
  `trip_id` bigint(20) DEFAULT NULL,
  `is_container_sealed` tinyint(1) DEFAULT NULL,
  `hidden_lock_present` tinyint(1) DEFAULT NULL,
  `tarpaulin_present` tinyint(1) DEFAULT NULL,
  `cargonet_hooks_present` tinyint(1) DEFAULT NULL,
  `created_at` bigint(20) DEFAULT NULL,
  `last_updated_at` bigint(20) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `vpi_trip_type_trip_id` (`trip_type`,`trip_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `volume_details`;
CREATE TABLE `volume_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `breadth` double DEFAULT NULL,
  `consignment_id` bigint(20) DEFAULT NULL,
  `height` double DEFAULT NULL,
  `length` double DEFAULT NULL,
  `number_of_boxes` int(11) DEFAULT NULL,
  `unit` varchar(255) DEFAULT NULL,
  `volume` double DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmtwe7i2an0dv8gxdcxtgesovb` (`consignment_id`),
  KEY `vd_index3` (`created_by_id`),
  KEY `vd_index4` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `zone`;
CREATE TABLE `zone` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `code` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  `type` varchar(40) NOT NULL DEFAULT 'PICKUP_AND_DELIVERY',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ogsepej3d4erg3m06qmyat27i` (`code`),
  KEY `FK7dbpp4hl0fva68sqg0cpdb15t` (`created_by_id`),
  KEY `FKbo8ta8wet7ybiu1mlsjuwtt7v` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `zoom_properties`;
CREATE TABLE `zoom_properties` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `variable_name` varchar(255) NOT NULL,
  `spring_profile` varchar(255) DEFAULT NULL,
  `variable_value` text,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `is_session_property` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `zp_variable_name` (`variable_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `zoom_user`;
CREATE TABLE `zoom_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  `location_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `zoom_user_type` varchar(127) DEFAULT NULL,
  `bp_id` bigint(20) DEFAULT NULL,
  `pickup_support` tinyint(1) DEFAULT '0',
  `drop_support` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `FK1wkfuiyyc6u5ljyqk3cn65yu6` (`user_id`),
  UNIQUE KEY `zu_index6` (`email`),
  KEY `FKmncmk9orvsth10kniubc4lp6e` (`created_by_id`),
  KEY `FK1t7kuo7bjvbkxoi5b2gge0jqk` (`last_updated_by_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `zoom_user_location_mapping`;
CREATE TABLE `zoom_user_location_mapping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `location_type` enum('BRANCH','PROCESSING_CENTER','CLUSTER','REGION','HEADQUARTER','BP_TERMINAL') NOT NULL,
  `location_id` bigint(20) NOT NULL,
  `zoom_user_id` bigint(20) NOT NULL,
  `is_active` tinyint(1) DEFAULT NULL,
  `deleted_at` bigint(20) DEFAULT NULL,
  `created_at` bigint(20) NOT NULL,
  `last_updated_at` bigint(20) NOT NULL,
  `created_by_id` bigint(20) DEFAULT NULL,
  `last_updated_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `zulm_zoom_user_id` (`zoom_user_id`),
  KEY `zulm_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



