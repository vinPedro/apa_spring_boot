-- Adiciona a especialidade padrão
MERGE INTO especialidade (id, nome, descricao)
    KEY(id)
    VALUES (1, 'OUTRO', 'Especialidade padrão do sistema.');