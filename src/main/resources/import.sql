INSERT INTO blog_user (id,uuid, username, password, name, email,status) VALUES (1,'1qaz2wsx', 'admin', 'admin', '叶胖', 'i@yepang.com',1);
INSERT INTO blog_user (id,uuid, username, password, name, email,status)  VALUES (2,'3edc4rfv', 'yseventeen', '123456', 'yseventeen', 'yseventeen@163.com',1);

INSERT INTO blog_authority (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO blog_authority (id, name) VALUES (2, 'ROLE_USER');

INSERT INTO blog_user_authority (blog_user_id, blog_authority_id) VALUES (1, 1);
INSERT INTO blog_user_authority (blog_user_id, blog_authority_id) VALUES (2, 2);
