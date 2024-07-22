import os
from sqlalchemy import create_engine, Column, Integer, String, Float, Time, Date, ForeignKey
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, relationship
from dotenv import load_dotenv

DATABASE_URL = os.getenv("DATABASE_URL")

Base = declarative_base()
engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

class Usuario(Base):
    __tablename__ = 'usuario'
    id = Column(Integer, primary_key=True, index=True)
    nome = Column(String, index=True)
    sobrenome = Column(String, index=True)
    sexo = Column(String, index=True)
    data_nascimento = Column(Date, index=True)
    email = Column(String, unique=True, index=True)
    senha = Column(String)
    pontos = relationship("Ponto", secondary="ponto_usuario", back_populates="usuarios")

class Ponto(Base):
    __tablename__ = 'ponto'
    id = Column(Integer, primary_key=True, index=True)
    id_usuario = Column(Integer)
    latitude = Column(Float)
    longitude = Column(Float)
    atividade = Column(String)
    horario = Column(Time)
    dias_semana = Column(String)
    usuarios = relationship("Usuario", secondary="ponto_usuario", back_populates="pontos")

class PontoUsuario(Base):
    __tablename__ = 'ponto_usuario'
    id = Column(Integer, primary_key=True, index=True)
    id_usuario = Column(Integer, ForeignKey('usuario.id'))
    id_ponto = Column(Integer, ForeignKey('ponto.id'))

Base.metadata.create_all(bind=engine)
