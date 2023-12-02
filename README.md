# PilaCoin

Este projeto PilaCoin utiliza o Docker para simplificar sua construção e execução. Siga as instruções abaixo para configurar e executar o projeto.

## Pré-requisitos

- Java 17
- Maven
- Docker

## Instruções para Execução

### 1. Baixar Dependências e Instalar o Projeto

Abra um terminal e execute o seguinte comando para baixar as dependências e instalar o projeto:

```bash
mvn clean dependency:resolve package -DskipTests
```

### 2. Construir a Imagem Docker

Depois de instalar o projeto, construa a imagem Docker executando o seguinte comando:

```bash
docker build -t pilacoin .
```

### 3. Rodar a Imagem Docker

Para iniciar a aplicação usando a imagem Docker, execute o seguinte comando:

```bash
docker run -p 8080:8080 pilacoin
```

O projeto estará disponível em [http://localhost:8080](http://localhost:8080).

## Observações

Certifique-se de ajustar as portas conforme necessário, especialmente se a porta 8080 estiver em uso.

Lembre-se de substituir `pilacoin` pelo nome desejado para a sua imagem Docker, caso seja necessário.

---

## Endpoints da API

Este projeto PilaCoin disponibiliza os seguintes endpoints:

    /api/bloco
    /api/dificuldade
    /api/pilaCoin
    /api/transacao
    /api/validacaoBloco
    /api/validacaoPilaCoin

Todos os endpoints são do tipo GET e podem ser acessados para obter informações específicas sobre blocos, dificuldade, PilaCoin, transações, validação de blocos e validação da PilaCoin.

---
Divirta-se explorando o mundo do PilaCoin! 😄