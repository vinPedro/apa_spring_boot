
-- 1. Cria o 'Usuario' para o Admin
MERGE INTO tb_usuarios (id, login, senha, role)
    KEY(id)
    VALUES (1, 'admin', '$2a$10$8.SjF.IUP53kF8F/y.TjGe.BYtIS./yE6A21e1R1iE.q.TjGe.BYt', 'ROLE_ADMIN');

-- 2. Cria o 'Admin' e o vincula ao Usuario (id=1)
MERGE INTO admin (id, usuario_id)
    KEY(id)
    VALUES (1, 1);

-- 3. Adiciona a especialidade padrão
MERGE INTO especialidade (id, nome, descricao)
    KEY(id)
    VALUES (1, 'OUTRO', 'Especialidade padrão do sistema.');