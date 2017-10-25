INSERT INTO `organization` (`id`, `name`, `organization_code`, `organization_type`, `status`, `use_actual_weight`, `insurance_applicable`) VALUES
(1,	'RIVIGO',	'RIVIGO',	'RIVIGO',	'ACTIVE',	0,	0),
(9999,	'RIVIGO_GLOBAL',	'GLOBAL',	'RIVIGO',	'ACTIVE',	0,	0);

INSERT INTO `zone` (`id`, `created_at`, `last_updated_at`, `code`, `status`, `location_id`, `type`)
VALUES (1, '1504516517361', '1504516517361', 'DEL_TEST', 'ACTIVE', '15', 'PICKUP_AND_DELIVERY');

INSERT INTO `zone` (`id`, `created_at`, `last_updated_at`, `code`, `status`, `location_id`, `type`)
VALUES (2, '1504516517361', '1504516517361', 'BOM_TEST', 'ACTIVE', '13', 'PICKUP_AND_DELIVERY');

INSERT INTO `zone` (`id`, `created_at`, `last_updated_at`, `code`, `status`, `location_id`, `type`)
VALUES (3, '1504516517361', '1504516517361', 'BLR_TEST', 'ACTIVE', '12', 'PICKUP_AND_DELIVERY');

INSERT INTO `zone` (`id`, `created_at`, `last_updated_at`, `code`, `status`, `location_id`, `type`)
VALUES (4, '1504516517361', '1504516517361', 'DEL_BO_TEST', 'ACTIVE', '36', 'PICKUP_AND_DELIVERY');

INSERT INTO `zone` (`id`, `created_at`, `last_updated_at`, `code`, `status`, `location_id`, `type`)
VALUES (5, '1504516517361', '1504516517361', 'BOM_BO_TEST', 'ACTIVE', '60', 'PICKUP_AND_DELIVERY');

INSERT INTO `zone` (`id`, `created_at`, `last_updated_at`, `code`, `status`, `location_id`, `type`)
VALUES (6, '1504516517361', '1504516517361', 'BLR_BO_TEST', 'ACTIVE', '115', 'PICKUP_AND_DELIVERY');

INSERT INTO `pincode` (`created_at`, `last_updated_at`, `code`, `status`, `pickup_zone_id`, `delivery_zone_id`, `state`, `pincode_service_type`)
VALUES ('1504516517361', '1504516517361', '000001', 'ACTIVE', (SELECT id from zone where code = 'DEL_TEST'), (SELECT id from zone where code = 'DEL_TEST'), 'Delhi', 'SERVICEABLE');

INSERT INTO `pincode` (`created_at`, `last_updated_at`, `code`, `status`, `pickup_zone_id`, `delivery_zone_id`, `state`, `pincode_service_type`)
VALUES ('1504516517361', '1504516517361', '000002', 'ACTIVE', (SELECT id from zone where code = 'BOM_TEST'), (SELECT id from zone where code = 'BOM_TEST'), 'Maharashtra', 'SERVICEABLE');

INSERT INTO `pincode` (`created_at`, `last_updated_at`, `code`, `status`, `pickup_zone_id`, `delivery_zone_id`, `state`, `pincode_service_type`)
VALUES ('1504516517361', '1504516517361', '000003', 'ACTIVE', (SELECT id from zone where code = 'BLR_TEST'), (SELECT id from zone where code = 'BLR_TEST'), 'Karnataka', 'SERVICEABLE');

INSERT INTO `pincode` (`created_at`, `last_updated_at`, `code`, `status`, `pickup_zone_id`, `delivery_zone_id`, `state`, `pincode_service_type`)
VALUES ('1504516517361', '1504516517361', '000004', 'ACTIVE', (SELECT id from zone where code = 'DEL_BO_TEST'), (SELECT id from zone where code = 'DEL_BO_TEST'), 'Delhi', 'SERVICEABLE');

INSERT INTO `pincode` (`created_at`, `last_updated_at`, `code`, `status`, `pickup_zone_id`, `delivery_zone_id`, `state`, `pincode_service_type`)
VALUES ('1504516517361', '1504516517361', '000005', 'ACTIVE', (SELECT id from zone where code = 'BOM_BO_TEST'), (SELECT id from zone where code = 'BOM_BO_TEST'), 'Maharashtra', 'SERVICEABLE');

INSERT INTO `pincode` (`created_at`, `last_updated_at`, `code`, `status`, `pickup_zone_id`, `delivery_zone_id`, `state`, `pincode_service_type`)
VALUES ('1504516517361', '1504516517361', '000006', 'ACTIVE', (SELECT id from zone where code = 'BLR_BO_TEST'), (SELECT id from zone where code = 'BLR_BO_TEST'), 'Karnataka', 'SERVICEABLE');

INSERT INTO `pincode` (`id`, `created_at`, `last_updated_at`, `code`, `status`, `created_by_id`, `last_updated_by_id`, `pickup_zone_id`, `delivery_zone_id`, `state`, `pincode_service_type`, `latitude`, `longitude`, `distance_to_pickup_branch`, `distance_to_pickup_pc`, `distance_to_delivery_branch`, `distance_to_delivery_pc`) VALUES
(319,	1460752217576,	1500895315082,	'560018',	'ACTIVE',	57,	949,	3,3,	'Karnataka',	'SERVICEABLE',	NULL,	NULL,	NULL,	NULL,	NULL,	NULL),
(1179,	1460752218038,	1474382903487,	'110017',	'ACTIVE',	57,	57,	1,	1,	'Delhi',	'SERVICEABLE',	NULL,	NULL,	NULL,	NULL,	NULL,	NULL);

INSERT INTO `user` (`created_at`, `last_updated_at`, `email`, `name`, `status`, `organization_id`) VALUES
(1460724268671,	1460724268671,	'master@rivigo.com',	'MASTER',	'INACTIVE',	1);

INSERT INTO `user` ( `created_at`, `last_updated_at`, `email`, `name`, `status`, `organization_id`) VALUES
(1467968017649,	1502514788321,	'chirag.bansal@rivigo.com',	'Chirag Bansal',	'ACTIVE',	1);
INSERT INTO `zoom_user` (`created_at`, `last_updated_at`, `email`, `status`, `location_id`, `user_id`, `zoom_user_type`, `pickup_support`, `drop_support`) VALUES
(1467968017691,	1502514788344,	'chirag.bansal@rivigo.com',	'ACTIVE',	15,	(SELECT id from `user` WHERE email='chirag.bansal@rivigo.com'),	'ZOOM_OA,ZOOM_TECH_SUPPORT,ZOOM_REPORTING_ADMIN,ZOOM_OPC_PLANNING,ZOOM_FINANCE_EXECUTIVE',	1,	1);


INSERT INTO `business_partner` (`created_at`, `last_updated_at`, `business_partner_code`, `name`, `status`, `bp_type`, `availability_status`) VALUES
(1466241891928,	1474878199155,	'test',	'test',	'ACTIVE',	'TERMINAL_OWNER',	'AVAILABLE');
INSERT INTO `user` (`created_at`, `last_updated_at`, `email`, `name`, `status`, `organization_id`) VALUES
(1501668197824,	1501668197826,	'test.bp@rivigo.com',	'TEST',	'ACTIVE',	1);
INSERT INTO `stockaccumulator` (`created_at`, `last_updated_at`, `email`, `name`, `status`, `stock_accumulator_role`, `accumulation_partner_id`, `user_id`, `availability_status`) VALUES
(1466241914008,	1466241914008,	'test.bp@rivigo.com',	'TEST',	'ACTIVE',	'STOCK_ACCUMULATOR_ADMIN',	(SELECT id from `business_partner` WHERE business_partner_code='test'),	(SELECT id from `user` WHERE email='test.bp@rivigo.com'),	'AVAILABLE');
INSERT INTO `stockaccumulator_zone` (`created_at`, `last_updated_at`, `drop_suppport`, `pickup_suppport`, `stockaccumulator_id`, `zone_id`)
VALUES ('1504516517361', '1504516517361', 1, 1, (SELECT id from stockaccumulator where email='test.bp@rivigo.com'), (SELECT id from zone where code = 'DEL_TEST'));

INSERT INTO `clients` (`id`, `created_at`, `last_updated_at`, `billing_name`, `client_code`, `display_name`, `name`, `sales_executive`, `status`, `industry_type_id`, `charge_basis`, `factor`, `volume_multiplier`, `service_type`, `cnote_type`, `has_billing_entity`, `insurance_reqd`, `is_business_agent`, `blocked_paid_for_delivery`, `blocked_topay_for_delivery`, `organization_id`, `insurance_excluded`) VALUES
(1, 1463504117860,	1473517982791,	'Test Client',	'TEST_CLIENT',	'Test Client',	'TEST_CLIENT',	'Test Client',	'ACTIVE',	(SELECT id from industry_type WHERE type = 'ECOMMERCE'),	'ACT',	1,	1,	'ZOOMEXPRESS',	'NORMAL',	0,	0,	0,	0,	0,	1,	0);

INSERT INTO `clients` (`id`, `created_at`, `last_updated_at`, `billing_name`, `client_code`, `display_name`, `name`, `sales_executive`, `status`, `website`, `created_by_id`, `last_updated_by_id`, `industry_type_id`, `charge_basis`, `factor`, `volume_multiplier`, `priority`, `service_type`, `cnote_type`, `has_billing_entity`, `appointment_keyword`, `mall_keyword`, `content_types`, `insurance_reqd`, `is_business_agent`, `blocked_paid_for_delivery`, `blocked_topay_for_delivery`, `organization_id`, `insurance_excluded`) VALUES
(21,	1460740039106,	1492684747172,	'AMAZON',	'AMZN',	'Amazon',	'Amazon',	'Varchas Bansal',	'ACTIVE',	'',	57,	750,	5,	'BAG',	18,	1,	'P1',	'ZOOM',	'NORMAL',	0,	NULL,	NULL,	NULL,	0,	0,	0,	0,	1,	0);

INSERT INTO `user` (`created_at`, `last_updated_at`, `email`, `name`, `status`, `organization_id`) VALUES
(1467968017649,	1502514788321,	'test.client@rivigo.com',	'Test Client',	'ACTIVE',	1);

INSERT INTO `client_user` (`created_at`, `last_updated_at`, `client_role`, `user_id`, `status`) VALUES
(1467978131152,	1467978131152,	'ADMIN',	(SELECT id from `user` WHERE email='test.client@rivigo.com'),	'ACTIVE');

INSERT INTO `client_user_mapping` (`created_at`, `last_updated_at`, `client_id`, `user_id`) VALUES
('1467394181704',	'1467394181704',	(SELECT id from `clients` WHERE client_code='TEST_CLIENT'),	(SELECT id from `user` WHERE email='test.client@rivigo.com'));

INSERT INTO `user` ( `created_at`, `last_updated_at`, `email`, `name`, `status`, `organization_id`) VALUES
(1467968017649,	1502514788321,	'test.captain.delt1@rivigo.com',	'Test Captain DELT1',	'ACTIVE',	1);
INSERT INTO `zoom_user` (`created_at`, `last_updated_at`, `email`, `status`, `location_id`, `user_id`, `zoom_user_type`, `pickup_support`, `drop_support`) VALUES
(1467968017691,	1502514788344, 'test.captain.delt1@rivigo.com', 'ACTIVE', 15,	(SELECT id from `user` WHERE email='test.captain.delt1@rivigo.com'),	'ZOOM_CAPTAIN,,,,',	1,	1);

INSERT INTO `user` ( `created_at`, `last_updated_at`, `email`, `name`, `status`, `organization_id`) VALUES
(1467968017649,	1502514788321,	'test.captain.bomt1@rivigo.com',	'Test Captain BOMT1',	'ACTIVE',	1);
INSERT INTO `zoom_user` (`created_at`, `last_updated_at`, `email`, `status`, `location_id`, `user_id`, `zoom_user_type`, `pickup_support`, `drop_support`) VALUES
(1467968017691,	1502514788344,	'test.captain.bomt1@rivigo.com',	'ACTIVE',	13,	(SELECT id from `user` WHERE email='test.captain.bomt1@rivigo.com'),	'ZOOM_CAPTAIN,,,,',	1,	1);

INSERT INTO `user` ( `created_at`, `last_updated_at`, `email`, `name`, `status`, `organization_id`) VALUES
(1467968017649,	1502514788321,	'test.captain.blrt1@rivigo.com',	'Test Captain BLRT1',	'ACTIVE',	1);
INSERT INTO `zoom_user` (`created_at`, `last_updated_at`, `email`, `status`, `location_id`, `user_id`, `zoom_user_type`, `pickup_support`, `drop_support`) VALUES
(1467968017691,	1502514788344,	'test.captain.blrt1@rivigo.com',	'ACTIVE',	12,	(SELECT id from `user` WHERE email='test.captain.blrt1@rivigo.com'),	'ZOOM_CAPTAIN,,,,',	1,	1);

INSERT INTO `app_login_restricted_location` (`app_id`, `login_restriction_type`, `restricted_location_id`, `created_at`, `last_updated_at`) VALUES ('captain_app',	'NO_RESTRICTIONS',	15,	1501806600000,	1501806600000);
INSERT INTO `app_login_restricted_location` (`app_id`, `login_restriction_type`, `restricted_location_id`, `created_at`, `last_updated_at`) VALUES ('captain_app',	'SINGLE_LOGIN',	13,	1501806600000,	1501806600000);
INSERT INTO `app_login_restricted_location` (`app_id`, `login_restriction_type`, `restricted_location_id`, `created_at`, `last_updated_at`) VALUES ('captain_app',	'NO_LOGIN',	12,	1501806600000,	1501806600000);

INSERT INTO `ou_drs_details` (`location_id`, `status`) VALUES ('5', 'BLOCKED');
INSERT INTO `ou_drs_details` (`location_id`, `status`) VALUES ('13', 'UNBLOCKED');