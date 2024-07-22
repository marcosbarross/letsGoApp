from fastapi import FastAPI, HTTPException, Depends
from pydantic import BaseModel, validator
from sqlalchemy.orm import Session
from models import SessionLocal, Usuario, Ponto, PontoUsuario
from typing import List
from datetime import datetime, time
import math

app = FastAPI()

class UsuarioAuth(BaseModel):
    email: str
    senha: str

class AuthResponse(BaseModel):
    status_code: int
    id_usuario: int
    email_usuario: str

class UsuarioCreate(BaseModel):
    nome: str
    sobrenome: str
    sexo: str
    data_nascimento: str
    email: str
    senha: str

class UsuarioDados(BaseModel):
    nome: str
    sobrenome: str
    sexo: str
    email: str

class PontoCreate(BaseModel):
    id_usuario: int
    latitude: float
    longitude: float
    atividade: str
    horario: str
    dias_semana: str

    class Config:
        from_attributes = True

    @validator('atividade', 'horario', 'dias_semana')
    def not_none(cls, v):
        if v is None:
            raise ValueError('Campo obrigatório')
        return v

class PontoBase(BaseModel):
    id_usuario: int
    id_ponto: int
    latitude: float
    longitude: float
    atividade: str
    horario: str
    dias_semana: str

class PontoOrdenado(BaseModel):
    id: int
    nome: str
    latitude: float
    longitude: float
    distancia: float
    atividade: str
    horario: str
    dias_semana: str


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@app.post("/auth/", response_model=AuthResponse)
def authenticate_usuario(usuario: UsuarioAuth, db: Session = Depends(get_db)):
    db_usuario = db.query(Usuario).filter(Usuario.email == usuario.email).first()
    if not db_usuario or db_usuario.senha != usuario.senha:
        raise HTTPException(status_code=401, detail="Email ou senha incorretos")
    
    return AuthResponse(status_code=200, id_usuario=db_usuario.id, email_usuario=db_usuario.email)

def parse_data_nascimento(data_nascimento_str: str) -> datetime.date:
    try:
        return datetime.strptime(data_nascimento_str, "%Y-%m-%d").date()
    except ValueError:
        try:
            return datetime.strptime(data_nascimento_str, "%d/%m/%Y").date()
        except ValueError:
            raise HTTPException(status_code=400, detail="Formato de data inválido. Use o formato dd/mm/aaaa ou yyyy-MM-dd.")

@app.post("/usuarios/", response_model=UsuarioCreate)
def create_usuario(usuario: UsuarioCreate, db: Session = Depends(get_db)):
    try:
        if db.query(Usuario).filter(Usuario.email == usuario.email).first():
            raise HTTPException(status_code=400, detail="Email já cadastrado")
        
        data_nascimento = parse_data_nascimento(usuario.data_nascimento)

        db_usuario = Usuario(
            nome=usuario.nome,
            sobrenome=usuario.sobrenome,
            sexo=usuario.sexo,
            data_nascimento=data_nascimento,
            email=usuario.email,
            senha=usuario.senha
        )
        db.add(db_usuario)
        db.commit()
        db.refresh(db_usuario)
        
        usuario_create_response = UsuarioCreate(
            nome=db_usuario.nome,
            sobrenome=db_usuario.sobrenome,
            sexo=db_usuario.sexo,
            data_nascimento=db_usuario.data_nascimento.strftime("%d/%m/%Y"),
            email=db_usuario.email,
            senha=db_usuario.senha
        )
        
        return usuario_create_response
    except HTTPException as e:
        raise e
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=400, detail=f"Erro ao criar usuário: {e}")
    
@app.get("/getUsuarioById/{id}", response_model = UsuarioDados)
def get_usuario_por_id(id: int, db: Session = Depends(get_db)):
    usuario = db.query(Usuario).filter(Usuario.id == id).first()
    return UsuarioDados (
        nome = usuario.nome,
        sobrenome = usuario.sobrenome,
        sexo = usuario.sexo,
        email = usuario.email
    )
    
@app.get("/usuarios/{usuario_id}/pontos", response_model=List[PontoBase])
def get_pontos_do_usuario(usuario_id: int, db: Session = Depends(get_db)):
    usuario = db.query(Usuario).filter(Usuario.id == usuario_id).first()
    if not usuario:
        raise HTTPException(status_code=404, detail="Usuário não encontrado")
    
    pontos = db.query(Ponto).join(PontoUsuario).filter(PontoUsuario.id_usuario == usuario_id).all()
    return [
        PontoBase(
            id_usuario=p.id_usuario,
            id_ponto=p.id,
            latitude=p.latitude,
            longitude=p.longitude,
            atividade=p.atividade,
            horario=p.horario.strftime("%H:%M"),
            dias_semana=p.dias_semana
        ) for p in pontos
    ]

@app.post("/pontos/", response_model=PontoCreate)
def create_ponto(ponto: PontoCreate, db: Session = Depends(get_db)):
    db_ponto = Ponto(
        id_usuario=ponto.id_usuario,
        latitude=ponto.latitude,
        longitude=ponto.longitude,
        atividade=ponto.atividade,
        horario=datetime.strptime(ponto.horario, "%H:%M").time(),
        dias_semana=ponto.dias_semana
    )
    db.add(db_ponto)
    db.commit()
    db.refresh(db_ponto)
    return PontoCreate(
        id_usuario=db_ponto.id_usuario,
        latitude=db_ponto.latitude,
        longitude=db_ponto.longitude,
        atividade=db_ponto.atividade,
        horario=db_ponto.horario.strftime("%H:%M"),
        dias_semana=db_ponto.dias_semana
    )

@app.get("/pontos/", response_model=List[PontoCreate])
def get_pontos(db: Session = Depends(get_db)):
    pontos = db.query(Ponto).all()
    return [
        PontoCreate(
            id_usuario=p.id_usuario,
            latitude=p.latitude,
            longitude=p.longitude,
            atividade=p.atividade,
            horario=p.horario.strftime("%H:%M"),
            dias_semana=p.dias_semana
        ) for p in pontos
    ]

def calcular_distancia(lat1, lon1, lat2, lon2):
    R = 6371.0
    d_lat = math.radians(lat2 - lat1)
    d_lon = math.radians(lon2 - lon1)
    a = math.sin(d_lat / 2)**2 + math.cos(math.radians(lat1)) * math.cos(math.radians(lat2)) * math.sin(d_lon / 2)**2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    distancia = R * c
    return distancia

@app.get("/pontos/ordenados", response_model=List[PontoOrdenado])
def get_pontos_ordenados(latitude: float, longitude: float, db: Session = Depends(get_db)):
    pontos = db.query(Ponto).all()
    pontos_ordenados = []
    for ponto in pontos:
        distancia = calcular_distancia(latitude, longitude, ponto.latitude, ponto.longitude)
        pontos_ordenados.append(PontoOrdenado(
            id=ponto.id,
            nome=ponto.atividade,
            latitude=ponto.latitude,
            longitude=ponto.longitude,
            distancia=distancia,
            atividade=ponto.atividade,
            horario=ponto.horario.strftime("%H:%M"),
            dias_semana=ponto.dias_semana
        ))
    pontos_ordenados.sort(key=lambda x: x.distancia)
    return pontos_ordenados

@app.post("/pontos/{ponto_id}/entrar")
def entrar_no_ponto(ponto_id: int, usuario_email: str, db: Session = Depends(get_db)):
    usuario = db.query(Usuario).filter(Usuario.email == usuario_email).first()
    if not usuario:
        raise HTTPException(status_code=404, detail="Usuário não encontrado")
    
    ponto_usuario = db.query(PontoUsuario).filter(PontoUsuario.id_usuario == usuario.id, PontoUsuario.id_ponto == ponto_id).first()
    if ponto_usuario:
        raise HTTPException(status_code=400, detail="Usuário já está no ponto")
    
    novo_ponto_usuario = PontoUsuario(id_usuario=usuario.id, id_ponto=ponto_id)
    db.add(novo_ponto_usuario)
    db.commit()
    db.refresh(novo_ponto_usuario)
    return {"detail": "Usuário entrou no ponto com sucesso"}

@app.post("/pontos/{ponto_id}/sair")
def sair_do_ponto(ponto_id: int, usuario_email: str, db: Session = Depends(get_db)):
    usuario = db.query(Usuario).filter(Usuario.email == usuario_email).first()
    if not usuario:
        raise HTTPException(status_code=404, detail="Usuário não encontrado")
    
    ponto_usuario = db.query(PontoUsuario).filter(PontoUsuario.id_usuario == usuario.id, PontoUsuario.id_ponto == ponto_id).first()
    if not ponto_usuario:
        raise HTTPException(status_code=400, detail="Usuário não está no ponto")
    
    db.delete(ponto_usuario)
    db.commit()
    return {"detail": "Usuário saiu do ponto com sucesso"}

@app.get("/pontos/{ponto_id}/usuarios", response_model=List[UsuarioCreate])
def get_usuarios_do_ponto(ponto_id: int, db: Session = Depends(get_db)):
    ponto = db.query(Ponto).filter(Ponto.id == ponto_id).first()
    if not ponto:
        raise HTTPException(status_code=404, detail="Ponto não encontrado")
    
    usuarios = db.query(Usuario).join(PontoUsuario).filter(PontoUsuario.id_ponto == ponto_id).all()
    return [UsuarioCreate(
        nome=u.nome,
        sobrenome=u.sobrenome,
        sexo=u.sexo,
        data_nascimento=u.data_nascimento.strftime("%d/%m/%Y"),
        email=u.email,
        senha=u.senha
    ) for u in usuarios]