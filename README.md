# 📄 Documentação da API: Sistema de Comissões e Multi-Tenant (v1.0)

## 🌟 Visão Geral do Sistema

O **Sistema de Comissões** é uma aplicação **SaaS (Software as a Service)** construída com Spring Boot/JPA que segue a arquitetura **Multi-Tenant (Múltiplos Clientes)**.

* **Multi-Tenant:** Cada cliente (representado pela entidade `Empresa`) tem seus dados isolados e acessíveis apenas por seus próprios usuários (`ROLE_ADMIN`, `ROLE_VENDEDOR`).
* **Modularidade:** As funcionalidades (ex: **`COMISSAO_CORE`**) são produtos (`Modulo`) que o Super Admin pode "vender" e ativar para clientes específicos. O acesso aos endpoints do módulo de Comissões é protegido por essa checagem.

## 🔒 Segurança e Convenções

| Detalhe            | Configuração                                                          |
| :----------------- | :-------------------------------------------------------------------- |
| **URL Base** | `http://localhost:8080/api`                                           |
| **Autenticação** | Token JWT obrigatório no cabeçalho `Authorization: Bearer <seu_token>`. |
| **Roles** | `ROLE_SUPER_ADMIN` (acesso total), `ROLE_ADMIN` (administrador da empresa-cliente), `ROLE_VENDEDOR` (usuário associado ao Vendedor). |
| **Módulo Obrigatório** | Endpoints de Comissões/Vendedores exigem o módulo `COMISSAO_CORE` ativo. |
| **Data de Criação** | Campos como `dataCadastro` (Empresa) e `dataCriacao` (User) são preenchidos automaticamente pelo `@CreationTimestamp`. |

---

## 1. Autenticação (`/api/auth`)

Endpoints públicos para login.

### `POST /api/auth/login`

| Detalhe       | Descrição                                                                                             | Status Sucesso |
| :------------ | :---------------------------------------------------------------------------------------------------- | :------------- |
| **Descrição** | Autentica um usuário e retorna o Token JWT e as chaves dos módulos ativos para sua empresa. | `200 OK`       |

**Requisição (Body - JSON): `LoginRequest`**
```json
{
  "email": "admin@empresa.com",
  "senha": "senha123"
}
```
**Resposta Sucesso (200 OK): `LoginResponse`**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "permissoesModulos": [
    "COMISSAO_CORE",
    "RELATORIOS_BASICOS"
  ]
}
```
---

## 2. Acesso Super Admin (`/api/superadmin`)

Acesso restrito a usuários com `ROLE_SUPER_ADMIN`.

### 2.1. Gerenciamento de Módulos (`/api/superadmin/modulos`)

| Método | URL                          | Descrição                                                                               |
| :----- | :--------------------------- | :-------------------------------------------------------------------------------------- |
| `POST` | `/api/superadmin/modulos`    | Cria um novo módulo no catálogo.                                                        |
| `GET`  | `/api/superadmin/modulos`    | Lista todos os módulos.                                                                 |
| `GET`  | `/api/superadmin/modulos/{id}` | Busca um módulo pelo ID.                                                                |
| `PUT`  | `/api/superadmin/modulos/{id}` | **(NOVO DETALHE)** Atualiza os dados de um módulo existente.                             |
| `GET`  | `/api/superadmin/modulos/disponiveis` | Lista módulos com status `PRONTO_PARA_PRODUCAO`.                                      |

#### `POST /api/superadmin/modulos`
**Requisição (Body - JSON): `ModuloRequestDTO`**
```json
{
  "nome": "Módulo de Helpdesk",
  "chave": "HELPDESK_CORE",
  "status": "PRONTO_PARA_PRODUCAO",
  "descricaoCurta": "Sistema de tickets básico.",
  "precoMensal": 99.90,
  "isPadrao": true
}
```
**Resposta Sucesso (201 Created): `Modulo` (Entidade)**
```json
{
  "id": 1,
  "nome": "Módulo de Helpdesk",
  "chave": "HELPDESK_CORE",
  "status": "PRONTO_PARA_PRODUCAO",
  "descricaoCurta": "Sistema de tickets básico.",
  "precoMensal": 99.90,
  "isPadrao": true
}
```

#### `PUT /api/superadmin/modulos/{id}` **(NOVO DETALHE)**
**Requisição (Body - JSON): `ModuloRequestDTO`**
```json
{
  "nome": "Módulo de Helpdesk Avançado",
  "chave": "HELPDESK_CORE",
  "status": "PRONTO_PARA_PRODUCAO",
  "descricaoCurta": "Sistema de tickets avançado com SLA.",
  "precoMensal": 149.90,
  "isPadrao": false
}
```
**Resposta Sucesso (200 OK): `Modulo` (Entidade Atualizada)**
```json
{
  "id": 1,
  "nome": "Módulo de Helpdesk Avançado",
  "chave": "HELPDESK_CORE",
  "status": "PRONTO_PARA_PRODUCAO",
  "descricaoCurta": "Sistema de tickets avançado com SLA.",
  "precoMensal": 149.90,
  "isPadrao": false
}
```

### 2.2. Gerenciamento de Empresas (`/api/superadmin/empresas`)

| Método | URL                             | Descrição                                                                               |
| :----- | :------------------------------ | :-------------------------------------------------------------------------------------- |
| `POST` | `/api/superadmin/empresas`        | Cria nova empresa, o primeiro `ROLE_ADMIN` e associa módulos padrão.                      |
| `GET`  | `/api/superadmin/empresas`        | Lista todas as empresas.                                                                |
| `GET`  | `/api/superadmin/empresas/{id}`   | Busca uma empresa pelo ID.                                                                |
| `PUT`  | `/api/superadmin/empresas/{id}`   | **(NOVO DETALHE)** Atualiza dados básicos da empresa (`nomeFantasia`, `cnpj`).            |
| `PUT`  | `/api/superadmin/empresas/{id}/modulos` | Vende/Associa um novo conjunto de módulos, substituindo os módulos ativos.           |

#### `POST /api/superadmin/empresas`
**Requisição (Body - JSON): `EmpresaRequestDTO`**
```json
{
  "nomeFantasia": "Tech Solutions LTDA",
  "cnpj": "11.222.333/0001-44",
  "adminNome": "Pedro Admin",
  "adminEmail": "admin@techsolutions.com",
  "adminSenha": "senhaforte123"
}
```
**Resposta Sucesso (201 Created): `Empresa` (Entidade)**
```json
{
  "id": 10,
  "nomeFantasia": "Tech Solutions LTDA",
  "cnpj": "11.222.333/0001-44",
  "dataCadastro": "2024-10-25T03:00:00",
  "modulosAtivos": [
    {
      "id": 1,
      "nome": "Sistema de Comissões",
      "chave": "COMISSAO_CORE",
      "status": "PRONTO_PARA_PRODUCAO",
      "descricaoCurta": "Gerenciamento completo...",
      "precoMensal": 150.00,
      "isPadrao": true
    }
  ]
}
```

#### `PUT /api/superadmin/empresas/{id}` **(NOVO DETALHE)**
**Requisição (Body - JSON): `EmpresaUpdateRequestDTO`**
```json
{
  "nomeFantasia": "Tech Solutions & Inovação LTDA",
  "cnpj": "11.222.333/0001-44"
}
```
**Resposta Sucesso (200 OK): `Empresa` (Entidade Atualizada)**
```json
{
  "id": 10,
  "nomeFantasia": "Tech Solutions & Inovação LTDA",
  "cnpj": "11.222.333/0001-44",
  "dataCadastro": "2024-10-25T03:00:00",
  "modulosAtivos": [
    {
      "id": 1,
      "nome": "Sistema de Comissões",
      "chave": "COMISSAO_CORE",
      "status": "PRONTO_PARA_PRODUCAO",
      "descricaoCurta": "Gerenciamento completo...",
      "precoMensal": 150.00,
      "isPadrao": true
    }
  ]
}
```

#### `PUT /api/superadmin/empresas/{id}/modulos`
**Requisição (Body - JSON): `AtualizarModulosEmpresaRequestDTO`**
```json
{
  "moduloIds": [ 1, 3 ]
}
```
**Resposta Sucesso (200 OK): `Empresa` (Entidade Atualizada)**
```json
{
  "id": 10,
  "nomeFantasia": "Tech Solutions LTDA",
  "cnpj": "11.222.333/0001-44",
  "dataCadastro": "2024-10-25T03:00:00",
  "modulosAtivos": [
    {
      "id": 1,
      "nome": "Sistema de Comissões",
      "chave": "COMISSAO_CORE",
       "status": "PRONTO_PARA_PRODUCAO",
      "descricaoCurta": "Gerenciamento completo...",
      "precoMensal": 150.00,
      "isPadrao": true
    },
    {
      "id": 3,
      "nome": "Relatórios Avançados",
      "chave": "RELATORIOS_AVANCADOS",
      "status": "PRONTO_PARA_PRODUCAO",
      "descricaoCurta": "Geração de relatórios...",
      "precoMensal": 120.50,
      "isPadrao": false
    }
  ]
}
```

### 2.3. Gerenciamento de Usuários Admin (`/api/superadmin/empresas/{empresaId}/admins`) **(NOVO)**

#### `POST /api/superadmin/empresas/{empresaId}/admins` **(NOVO)**
**Requisição (Body - JSON): `AdminUsuarioRequestDTO` (Exemplo)**
```json
{
  "nome": "Maria Gerente",
  "email": "maria.gerente@techsolutions.com",
  "senha": "outrasenhaforte456"
}
```
**Resposta Sucesso (201 Created): `User` (Entidade do Usuário Criado)**
```json
{
  "id": 502,
  "nome": "Maria Gerente",
  "email": "maria.gerente@techsolutions.com",
  "role": "ROLE_ADMIN",
  "dataCriacao": "2025-10-25T21:10:00",
  "empresa": { "id": 10, "nomeFantasia": "Tech Solutions LTDA", "cnpj": "11.222.333/0001-44" }
}
```

---

## 3. Acesso Público (`/api/modulos`) **(NOVO)**

Endpoints públicos relacionados ao catálogo de Módulos.

### `GET /api/modulos/catalogo` **(NOVO)**

| Detalhe       | Descrição                                                                                             | Status Sucesso |
| :------------ | :---------------------------------------------------------------------------------------------------- | :------------- |
| **Descrição** | Retorna o catálogo público de módulos disponíveis para contratação (status `PRONTO_PARA_PRODUCAO`). | `200 OK`       |
| **Permissões**| Nenhuma (Público).                                                               |                |

**Resposta Sucesso (200 OK): `List<ModuloCatalogoDTO>`**
```json
[
  {
    "id": 1,
    "nome": "Sistema de Comissões",
    "chave": "COMISSAO_CORE",
    "descricaoCurta": "Gerenciamento completo de vendedores, vendas e comissões.",
    "precoMensal": 150.00
  },
  {
    "id": 2,
    "nome": "Relatórios Avançados",
    "chave": "RELATORIOS_AVANCADOS",
    "descricaoCurta": "Geração de relatórios personalizados e dashboards.",
    "precoMensal": 120.50
  }
]
```

---

## 4. Acesso Empresa Admin (`/api/empresa`) **(ATUALIZADO)**

Endpoints para o `ROLE_ADMIN` obter informações sobre sua própria empresa.
**Permissões:** Requer `ROLE_ADMIN`.

### `GET /api/empresa/me` **(NOVO)**

| Detalhe       | Descrição                                                                                                  | Status Sucesso |
| :------------ | :--------------------------------------------------------------------------------------------------------- | :------------- |
| **Descrição** | Retorna os detalhes da empresa do usuário ADMIN logado, incluindo a contagem de usuários com `ROLE_ADMIN`. | `200 OK`       |

**Resposta Sucesso (200 OK): `EmpresaDetalhesDTO`**
```json
{
  "id": 10,
  "nomeFantasia": "Tech Solutions LTDA",
  "razaoSocial": "Tech Solutions Desenvolvimento de Software LTDA",
  "cnpj": "11.222.333/0001-44",
  "dataCadastro": "2024-10-25T03:00:00",
  "qtdAdmins": 2
}
```

### `GET /api/empresa/meus-modulos`

| Detalhe       | Descrição                                                                      | Status Sucesso |
| :------------ | :----------------------------------------------------------------------------- | :------------- |
| **Descrição** | Lista os detalhes de todos os módulos que estão atualmente ativos para a empresa. | `200 OK`       |

**Resposta Sucesso (200 OK): `Set<Modulo>`** (Conjunto de entidades `Modulo` ativas).
```json
[
    {
      "id": 1,
      "nome": "Sistema de Comissões",
      "chave": "COMISSAO_CORE",
      "status": "PRONTO_PARA_PRODUCAO",
      "descricaoCurta": "Gerenciamento completo...",
      "precoMensal": 150.00,
      "isPadrao": true
    },
    {
      "id": 3,
      "nome": "Relatórios Avançados",
      "chave": "RELATORIOS_AVANCADOS",
      "status": "PRONTO_PARA_PRODUCAO",
      "descricaoCurta": "Geração de relatórios...",
      "precoMensal": 120.50,
      "isPadrao": false
    }
]
```

---

## 5. Módulo de Vendas e Comissões (`/api/vendedores`, `/api/vendas`, `/api/dashboard`)

Acesso restrito a usuários com `ROLE_ADMIN` e módulo `COMISSAO_CORE` ativo.

### 5.1. Gerenciamento de Vendedores (`/api/vendedores`)

| Método | URL                        | Descrição                                                                               |
| :----- | :------------------------- | :-------------------------------------------------------------------------------------- |
| `POST` | `/api/vendedores`            | Cria um novo vendedor e seu usuário `ROLE_VENDEDOR`.                                     |
| `PUT`  | `/api/vendedores/{id}`       | **(NOVO DETALHE)** Atualiza o `percentualComissao` de um vendedor.                     |
| `GET`  | `/api/vendedores/{id}`       | Busca dados resumidos de um vendedor (incluindo métricas).                               |
| `GET`  | `/api/vendedores`            | Lista todos os vendedores da empresa com métricas agregadas.                             |
| `GET`  | `/api/vendedores/{id}/detalhes` | Busca o vendedor com todas as métricas e o histórico mensal.                            |

#### `POST /api/vendedores`
**Requisição (Body - JSON): `VendedorRequestDTO`**
```json
{
  "nome": "Julia Campos",
  "email": "julia.campos@empresa.com",
  "percentualComissao": 7.00
}
```
**Resposta Sucesso (201 Created): `VendedorCriadoResponseDTO`**
```json
{
  "idVendedor": 101,
  "idUsuario": 501,
  "nome": "Julia Campos",
  "email": "julia.campos@empresa.com",
  "percentualComissao": 7.00,
  "idEmpresa": 10,
  "senhaTemporaria": "aBcDeF1234"
}
```

#### `PUT /api/vendedores/{id}` **(NOVO DETALHE)**
**Requisição (Body - JSON): `VendedorUpdateRequestDTO`**
```json
{
  "percentualComissao": 7.50
}
```
**Resposta Sucesso (200 OK): `VendedorResponseDTO`**
```json
{
  "idVendedor": 101,
  "percentualComissao": 7.50,
  "qtdVendas": 55,
  "valorTotalVendas": 25000.00,
  "nome": "Julia Campos",
  "email": "julia.campos@empresa.com"
}
```

#### `GET /api/vendedores` (Listar Todos)
**Resposta Sucesso (200 OK): `List<VendedorResponseDTO>`**
```json
[
  {
    "idVendedor": 101,
    "percentualComissao": 7.00,
    "qtdVendas": 55,
    "valorTotalVendas": 25000.00,
    "nome": "Julia Campos",
    "email": "julia.campos@empresa.com"
  },
  {
    "idVendedor": 102,
    "percentualComissao": 5.50,
    "qtdVendas": 12,
    "valorTotalVendas": 8000.50,
    "nome": "Roberto Silva",
    "email": "roberto.silva@empresa.com"
  }
]
```

#### `GET /api/vendedores/{id}` (Detalhes)
**Resposta Sucesso (200 OK): `VendedorResponseDTO`**
```json
{
  "idVendedor": 101,
  "percentualComissao": 7.00,
  "qtdVendas": 55,
  "valorTotalVendas": 25000.00,
  "nome": "Julia Campos",
  "email": "julia.campos@empresa.com"
}
```

#### `GET /api/vendedores/{id}/detalhes` (Detalhes e Histórico)
**Resposta Sucesso (200 OK): `VendedorDetalhadoResponseDTO`**
```json
{
  "id": 101,
  "nome": "Julia Campos",
  "email": "julia.campos@empresa.com",
  "percentualComissao": 7.00,
  "idEmpresa": 10,
  "dataCadastro": "2023-10-01T10:00:00",
  "qtdVendas": 55,
  "valorTotalVendas": 25000.00,
  "mediaComissao": 150.00,
  "historicoRendimentos": [
    { "mesAno": "2024-10", "valorVendido": 8000.00, "valorComissao": 560.00 },
    { "mesAno": "2024-09", "valorVendido": 12000.00, "valorComissao": 840.00 }
  ]
}
```

### 5.2. Gerenciamento de Vendas (`/api/vendas`)

| Método | URL           | Descrição                                                              |
| :----- | :------------ | :--------------------------------------------------------------------- |
| `POST` | `/api/vendas` | Lança uma nova venda e calcula o `valorComissaoCalculado`. |
| `GET`  | `/api/vendas` | Lista todas as vendas registradas na empresa logada.         |

#### `POST /api/vendas`
**Requisição (Body - JSON): `VendaRequestDTO`**
```json
{
  "vendedorId": 101,
  "valorVenda": 5200.00
}
```
**Resposta Sucesso (201 Created): `Venda` (Entidade)**
```json
{
  "id": 5012,
  "valorVenda": 5200.00,
  "valorComissaoCalculado": 364.00,
  "dataVenda": "2024-10-25T03:05:00",
  "vendedor": {
    "idVendedor": 101,
    "nome": "Julia Campos",
    "email": "julia.campos@empresa.com",
    "percentualComissao": 7.00
   },
  "empresa": {
    "id": 10,
    "nomeFantasia": "Tech Solutions LTDA",
    "cnpj": "11.222.333/0001-44",
    "dataCadastro": "2024-10-25T03:00:00",
    "modulosAtivos": []
  }
}
```
### 5.3. Dashboard Gerencial (`/api/dashboard`)

#### `GET /api/dashboard/empresa` **(ATUALIZADO)**
**Descrição:** Retorna todas as métricas consolidadas (totais do mês, rankings, maiores/últimas vendas e histórico).
**Status Sucesso:** `200 OK`.

**Resposta Sucesso (200 OK): `DashboardResponseDTO`**
```json
{
    "totalVendasMes": 55890.50,
    "totalComissoesMes": 3890.75,
    "qtdVendasMes": 125,
    "mediaVendaMes": 447.12,
    "mediaComissaoMes": 31.13,
    "rankingVendedores": [
        {
            "nomeVendedor": "Julia Campos",
            "idVendedor": 101,
            "valorTotal": 15200.00,
            "qtdVendas": 35
        },
        {
            "nomeVendedor": "Roberto Silva",
            "idVendedor": 102,
            "valorTotal": 12500.00,
            "qtdVendas": 28
        }
    ],
    "maioresVendas": [
        {
            "idVenda": 5012,
            "nomeVendedor": "Roberto Silva",
            "idVendedor": 102,
            "valorVenda": 5200.00,
            "dataVenda": "2024-10-25T10:30:00Z"
        },
        {
            "idVenda": 5005,
            "nomeVendedor": "Julia Campos",
            "idVendedor": 101,
            "valorVenda": 4100.00,
            "dataVenda": "2024-10-23T14:00:00Z"
        }
    ],
    "ultimasVendas": [
        {
            "idVenda": 5015,
            "nomeVendedor": "Julia Campos",
            "idVendedor": 101,
            "valorVenda": 150.00,
            "dataVenda": "2024-10-25T13:55:00Z"
        },
         {
            "idVenda": 5014,
            "nomeVendedor": "Ana Souza",
            "idVendedor": 103,
            "valorVenda": 450.90,
            "dataVenda": "2024-10-25T13:40:00Z"
        }
    ],
    "historicoVendasMensal": [
        {
            "mesAno": "2024-10",
            "valorVendido": 55890.50,
            "valorComissao": 3890.75
        },
        {
            "mesAno": "2024-09",
            "valorVendido": 62000.00,
            "valorComissao": 4800.00
        }
    ]
}
```

---

## 6. Acesso Geral - Usuário Logado (`/api/usuarios`) **(NOVO)**

Endpoints disponíveis para qualquer usuário autenticado gerenciar sua própria conta.

### `PUT /api/usuarios/me/senha` **(NOVO)**

**Descrição:** Permite que o usuário logado (Super Admin, Admin ou Vendedor) altere sua própria senha.
**Permissões:** Qualquer usuário autenticado.
**Status Sucesso:** `200 OK` (ou `204 No Content`).

**Requisição (Body - JSON): `AlterarSenhaRequestDTO` (Exemplo)**
```json
{
  "senhaAtual": "senha123",
  "novaSenha": "novaSenha456"
}
```
**Resposta Sucesso (200 OK ou 204 No Content):** (Sem corpo)