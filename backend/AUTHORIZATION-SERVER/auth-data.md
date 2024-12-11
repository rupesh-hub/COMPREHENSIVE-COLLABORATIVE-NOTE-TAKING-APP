http://localhost:9090/.well-known/openid-configuration

http://localhost:9090/oauth2/authorize
?client_id=note-taking-app
&response_type=code
&scope=openid
&redirect_uri=http://www.example.com/auth
&code_challenge=VHNoU7E5X6weEnjq4FtgslBPMad4fNjfVbrSZd0BInE
&code_challenge_method=S256

http://localhost:9090/oauth2/token?code=gDJPg7fs22c0f5r_532_bF-ka_bmAIf2MIMLNhBWBx45vEbVK0wq_5qlQ1lz5lDpp6_I49u9I_iH4c2f07eZYlY0vGkAaJKH3DxKN-gdY8OvJrjnYEi4Y5cXYcGbHOeE&grant_type=authorization_code&client_id=note-taking-app&redirect_uri=http://www.example.com/auth&code_verifier=o3l4IHMWC5ORsxUhU2XxUSSWy8SZT8viTw-v0C6wnOvVXG4n60uNwHPG4k9lHrOXunhVD0PpWfgd1aHrKKxcFJvJGa_7IYTcp42ZA-Ppw1lrHtc52yzhQk5S70ykGB0y


select * from _authentication_methods;
select * from _clients;
select * from _grant_types;
select * from _redirect_uris;
select * from _scopes;
select * from _token_settings;
select * from _client_authentication_method;
select * from _client_grant_type;
select * from _client_scope;
select * from _client_redirect_uri;

select * from _users;
select * from _images;
select * from _roles;
select * from _users_roles;


INSERT INTO _clients(id, created_at, modified_on, token_setting_id, client_id, name, secret)
VALUES (1, '2024-11-22 01:00:41.000000', '2024-11-22 01:00:44.000000', 1, '6d95a48d-ef2e-4e07-98be-d7ad0ee50b6e', 'note-taking-app', 'secret');

INSERT INTO _grant_types(id, grant_type)
VALUES (1, 'authorization_code'), (2, 'refresh_token'), (3, 'client_credentials');

INSERT INTO _redirect_uris(id, uri)
VALUES(1, 'http://www.example.com/auth');

INSERT INTO _scopes(id, scope)
VALUES (1, 'openid');

INSERT INTO _token_settings(id, access_token_ttl, authentication_token_ttl, refresh_token_ttl, token_type)
VALUES (1, 5, 5, 5, 'reference');

INSERT INTO _authentication_methods(id, authentication_method)
VALUES (1, 'client_secret_basic');

INSERT INTO _client_authentication_method(authentication_method_id, client_id)
VALUES (1, 1);

INSERT INTO _client_grant_type(client_id, grant_type_id) VALUES
(1,1), (1,2);

INSERT INTO _client_scope(client_id, scope_id) VALUES
(1, 1);

INSERT INTO _users(id, enabled, created_at, updated_at, created_by, email, first_name, last_name, password, updated_by, user_id, username)
VALUES (1, true, '2024-11-22 01:00:44.000000','2024-11-22 01:00:44.000000','rupesh','dulal.rupesh76@gmail.com','rupesh','dulal','$2a$10$RjCtw2C/tBXubmpa/lI4DuKAWWQ33oaf39zpC38TdehOxZVMFlSsq','rupesh','rupesh','rupesh');

INSERT INTO _roles(id, name) VALUES (1, 'user');
INSERT INTO _users_roles(role_id, user_id) VALUES (1, 1);