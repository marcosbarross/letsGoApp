#!/usr/bin/env python
# coding: utf-8

# In[2]:


get_ipython().system('pip install fastapi')


# In[5]:




pip install --upgrade pip


# In[7]:


get_ipython().system('pip install psycopg2')


# In[8]:


from fastapi import FastAPI, HTTPException, Depends  # Importa classes e funções necessárias do FastAPI
from pydantic import BaseModel  # Importa a classe BaseModel do Pydantic para definir modelos de dados
from typing import List  # Importa o tipo List para definir listas de elementos
from math import radians, sin, cos, sqrt, atan2  # Importa funções matemáticas para cálculos geográficos
from operator import itemgetter  # Importa a função itemgetter para ordenação de dicionários
from datetime import datetime, timedelta  # Importa classes para manipulação de datas e horas
import psycopg2  # Importa a biblioteca psycopg2 para conexão com o PostgreSQL


# In[11]:


conn = psycopg2.connect(  # Estabelece a conexão com o banco de dados PostgreSQL
    dbname="letsgoapp",
    user="postgres",
    password="4351",
    host="localhost"
)
cur = conn.cursor() 

conn.commit()  # Confirma as alterações no banco de dados


# In[13]:


class Pontodeencontro(BaseModel):
    cep: str
    rua: str
    numero: int
    complemento: str
    atividade: str
        
class Usuario(BaseModel):
    nome: str
    sobrenome: str
    sexo: str
    data_nascimento: str
    profissao: str
    cep: str
    endereco: str
    cpf: str
    senha: str
    email: str
    id_usuario: int

class UsuarioAuth(BaseModel):  # Define uma classe modelo para autenticação de usuários
    email: str
    senha: str
        
# Inicializa a aplicação FastAPI
app = FastAPI()


# In[16]:


# Define um endpoint para salvar um novo ponto de encontro
@app.post("/AddPontodeencontro/")
async def criar_pontodeencontro(pontodeencontro: Pontodeencontro):
    cur.execute("""
        INSERT INTO cadastro_pontodeencontro(cep, rua, numero, complemento, atividade)
        VALUES (%s,%s,%s,%s,%s)
        RETURNING id
    """, (pontodeencontro.cep, pontodeencontro.rua, pontodeencontro.numero, pontodeencontro.complemento, pontodeencontro.atividade))
    id = cur.fetchone()[0]
    conn.commit()
    return {"id": id, **pontodeencontro.dict()}

# Define um endpoint para retornar todos os pontos de encontros salvos
@app.get("/GetPontodeencontro/")
async def listar_pontosdeencontro():
    cur.execute("SELECT/ FROM pontosdeencontro")
    cadastro_pontodeencontro = []
    for row in cur.fetchall():
        id, cep, rua, numero, complemento, atividade = row
        cadastro_pontodeencontro.append({"id":id,"cep":cep,"rua":rua,"numero":numero,"complemento":complemento,"atividade": atividade})
    return cadastro_pontodeencontro
    


# In[22]:


# Define um endpoint para salvar um novo usuário

@app.post("/AddNovoUsuario/")
async def criar_novousuario(novousuario: Usuario):
    cur.execute("""
        INSERT INTO cadastro_usuarios(nome,sobrenome,sexo,data_nascimento,profissao,cep,endereco,cpf,senha,email, id_usuario)
        VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)
        RETURNING id
    """, (novousuario.nome, novousuario.sobrenome, novousuario.sexo, novousuario.data_nascimento, novousuario.profissao, novousuario.cep, novousuario.endereco, novousuario.cpf, novousuario.senha, novousuario.email, novousuario.id_usuario))
    id = cur.fetchone()[0]
    conn.commit()
    return {"nome": nome, "sobrenome": sobrenone, "sexo": sexo, "data_nascimento": data_nascimento, "profissao": profissao, "cep":cep, "endereco": endereco, "cpf": cpf, "senha": senha, "email": email, "id_usuario": id_usuario **novousuario.dict()}

# Define um endpoint para retornar todos os novos usuários salvos

@app.get("/GetNovoUsuario/")
async def listar_novousuario():
    cur.execute("SELECT/ FROM novousuario")
    criar_novousuario = []
    for row in cur.fetchall():
        nome, sobrenome, sexo, data_nascimento, profissao, cep, endereco, cpf, senha, email, id_usuario  = row
        criar_novousuario.append({"nome":nome, "sobrenome":sobrenome, "sexo":sexo, "data_nascimento": data_nascimento, "profissao":profissao, "cep":cep, "endereco":endereco, "cpf":cpf,"senha":senha,"email":email, "id_usuario": id_usuario})
    return criar_novousuario


# In[20]:


# Define um endpoint para autenticar usuários
@app.post("/AutenticarUsuario/")
async def autenticar_usuario(usuario: UsuarioAuth):
    email = usuario.email
    senha = usuario.senha
    cur.execute("SELECT id_usuario, nome, email, senha FROM cadastro_usuarios WHERE email = %s", (email,))
    usuario = cur.fetchone() 
    if usuario is None:
        raise HTTPException(status_code=404, detail="Usuário não encontrado")
    id_usuario, nome, email_db, senha_db = usuario
    if senha == senha_db:
        return {"status_code": 200, "id_usuario": id_usuario}  # Retorna o status de sucesso e o ID do usuário
    else:
        raise HTTPException(status_code=401, detail="Credenciais inválidas")  # Levanta uma exceção se as credenciais forem inválidas

# Define um endpoint para buscar usuário por ID
@app.get("/UsuarioPorId/{usuario_id}")
async def get_usuario_por_id(id_usuario: int):
    cur.execute("SELECT id_usuario, nome, email, senha FROM cadastro_usuarios WHERE id = %s", (id_usuario,))
    usuario = cur.fetchone()  # Obtém os dados do usuário com o ID fornecido
    if usuario is None:
        raise HTTPException(status_code=404, detail="Usuário não encontrado")  # Levanta uma exceção se o usuário não for encontrado
    
    id_usuario, nome, email_db, senha_db = usuario
    return {"id_usuario": id_usuario, "nome": nome, "email": email, "senha": senha}  # Retorna os dados do usuário


# In[ ]:




