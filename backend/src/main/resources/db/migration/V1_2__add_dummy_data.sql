-- Insert dummy data
INSERT INTO branch (id)
VALUES ('e246e2e6-d734-46a0-83e2-e6ee9b725977'),
       ('23aefea3-63ee-495e-b251-d079b632846c'),
       ('62ff7cb7-f8f1-4eaa-96b6-1c7b8e794df1'),
       ('0f7af2d7-d9c6-4234-a663-5a9ffbab8219');

INSERT INTO users (id, first_name, middle_name, last_name, branch_id)
VALUES ('11111111-1111-1111-1111-111111111111', 'Alice', 'M', 'Smith', 'e246e2e6-d734-46a0-83e2-e6ee9b725977'),
       ('22222222-2222-2222-2222-222222222222', 'Bob', NULL, 'Johnson', 'e246e2e6-d734-46a0-83e2-e6ee9b725977'),
       ('33333333-3333-3333-3333-333333333333', 'Charlie', 'A', 'Brown', '62ff7cb7-f8f1-4eaa-96b6-1c7b8e794df1');

INSERT INTO accounts (id, balance, user_id)
VALUES ('aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 1000.0, '11111111-1111-1111-1111-111111111111'),
       ('aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 2500.5, '11111111-1111-1111-1111-111111111111'),
       ('bbbbbbb1-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 500.0, '22222222-2222-2222-2222-222222222222'),
       ('bbbbbbb2-bbbb-bbbb-bbbb-bbbbbbbbbbb2', 1200.0, '22222222-2222-2222-2222-222222222222'),
       ('ccccccc1-cccc-cccc-cccc-ccccccccccc1', 3000.0, '33333333-3333-3333-3333-333333333333');

INSERT INTO deposits (id, amount, interest_rate, user_id)
VALUES ('c8426c76-7589-465d-bf9f-320352171cbc', 123.0, 1.2, '11111111-1111-1111-1111-111111111111'),
       ('5d618eec-f5db-4196-b6ab-25c370a5b1f5', 321.0, 1.8, '11111111-1111-1111-1111-111111111111'),
       ('bdad9348-057c-43c4-9c27-01df78d3cb27', 13.0, 1.65, '22222222-2222-2222-2222-222222222222'),
       ('6837527c-02cf-42cd-82bf-d9f03433cc9d', 43.0, 1.8, '33333333-3333-3333-3333-333333333333'),
       ('6083e9d4-8125-476c-924e-84b341ff0161', 15.0, 1.3, '33333333-3333-3333-3333-333333333333');

INSERT INTO fee_tax_transaction (id, amount, user_id, created_at)
VALUES ('eeeeeee1-eeee-eeee-eeee-eeeeeeeeeee1', 10.0, '11111111-1111-1111-1111-111111111111', NOW()),
       ('eeeeeee2-eeee-eeee-eeee-eeeeeeeeeee2', 15.5, '22222222-2222-2222-2222-222222222222', NOW()),
       ('eeeeeee3-eeee-eeee-eeee-eeeeeeeeeee3', 20.0, '33333333-3333-3333-3333-333333333333', NOW());

INSERT INTO investment (id, risk, balance, user_id)
VALUES ('9b73550d-b71e-4ddf-8467-972d357ec7f2', 3, 1000.0, '11111111-1111-1111-1111-111111111111'),
       ('04e0b6b4-fe2b-4a98-9bcb-268b1332f122', 5, 1500.0, '11111111-1111-1111-1111-111111111111'),
       ('7608bb1b-c85b-4187-945b-7e1241c0baa7', 2, 500.0, '22222222-2222-2222-2222-222222222222'),
       ('9ed85173-838f-4144-8f9d-59a247a64242', 4, 1200.0, '22222222-2222-2222-2222-222222222222'),
       ('6443651f-84b7-4fe8-9e33-115e53f57179', 1, 3000.0, '33333333-3333-3333-3333-333333333333');

INSERT INTO transactions (id, amount, timestamp, user_id, type, description)
VALUES ('ff2c031c-d1b7-409b-ab6a-a63a16ccdd3b', 500.0, NOW(), '11111111-1111-1111-1111-111111111111', 'TOP_UP',
        'Initial deposit for Alice'),
       ('d92dd9c1-36cc-420c-ab30-6996d2e67d3f', 50.0, NOW(), '11111111-1111-1111-1111-111111111111', 'FEE',
        'Service fee for Alice'),
       ('43cdfd95-db4e-4746-a198-e583b8345717', 1200.0, NOW(), '22222222-2222-2222-2222-222222222222', 'TOP_UP',
        'Initial deposit for Bob'),
       ('4e461060-074f-4f5b-82ec-cd3a33de71bd', 100.0, NOW(), '22222222-2222-2222-2222-222222222222', 'FEE',
        'Tax deducted for Bob'),
       ('5c95e7a8-73c9-4bbe-84e7-67983fa6e853', 3000.0, NOW(), '33333333-3333-3333-3333-333333333333', 'TOP_UP',
        'Initial deposit for Charlie');

INSERT INTO auth_users_details (id, username, email, password, role, user_id)
VALUES ('673f26a8-c6ca-43af-b575-030cb6260ccc', 'admin_user', 'adminuser@integrabank.com',
        '$2a$10$KXGNMZb91BgDLcuPQJjusuuRLIdUIKaQZ0GFY6RBUOoLuhzJisDhe',
        'ADMIN','11111111-1111-1111-1111-111111111111'),
        ('ba22262c-ba38-4f48-a3fb-526eac8956ea', 'regular_user', 'user@gmail.com',
        '$2a$10$TBgA1LLt3Vq9zO4xd6NZTekGPpiJT2CnmQw.Vl6ZBQwHMrAn8dbHO',
        'USER', '33333333-3333-3333-3333-333333333333');

