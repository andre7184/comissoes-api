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

**Resposta Sucesso (200 OK): `LoginResponse`**

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
**Resposta Sucesso (201 Created): `Modulo` (Entidade)**

#### `PUT /api/superadmin/modulos/{id}` **(NOVO DETALHE)**
**Requisição (Body - JSON): `ModuloRequestDTO`**
**Resposta Sucesso (200 OK): `Modulo` (Entidade Atualizada)**

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
**Resposta Sucesso (201 Created): `Empresa` (Entidade)**

#### `PUT /api/superadmin/empresas/{id}` **(NOVO DETALHE)**
**Requisição (Body - JSON): `EmpresaUpdateRequestDTO`**
**Resposta Sucesso (200 OK): `Empresa` (Entidade Atualizada)**

#### `PUT /api/superadmin/empresas/{id}/modulos`
**Requisição (Body - JSON): `AtualizarModulosEmpresaRequestDTO`**
**Resposta Sucesso (200 OK): `Empresa` (Entidade Atualizada)**

### 2.3. Gerenciamento de Usuários Admin (`/api/superadmin/empresas/{empresaId}/admins`) **(NOVO)**

#### `POST /api/superadmin/empresas/{empresaId}/admins` **(NOVO)**
**Requisição (Body - JSON): `AdminUsuarioRequestDTO` (Exemplo)**
**Resposta Sucesso (201 Created): `User` (Entidade do Usuário Criado)**

---

## 3. Acesso Público (`/api/modulos`) **(NOVO)**

Endpoints públicos relacionados ao catálogo de Módulos.

### `GET /api/modulos/catalogo` **(NOVO)**

| Detalhe       | Descrição                                                                                             | Status Sucesso |
| :------------ | :---------------------------------------------------------------------------------------------------- | :------------- |
| **Descrição** | Retorna o catálogo público de módulos disponíveis para contratação (status `PRONTO_PARA_PRODUCAO`). | `200 OK`       |
| **Permissões**| Nenhuma (Público).                                                               |                |

**Resposta Sucesso (200 OK): `List<ModuloCatalogoDTO>`**

---

## 4. Acesso Empresa Admin (`/api/empresa`) **(ATUALIZADO)**

Endpoints para o `ROLE_ADMIN` obter informações sobre sua própria empresa.
**Permissões:** Requer `ROLE_ADMIN`.

### `GET /api/empresa/me` **(NOVO)**

| Detalhe       | Descrição                                                                                                  | Status Sucesso |
| :------------ | :--------------------------------------------------------------------------------------------------------- | :------------- |
| **Descrição** | Retorna os detalhes da empresa do usuário ADMIN logado, incluindo a contagem de usuários com `ROLE_ADMIN`. | `200 OK`       |

**Resposta Sucesso (200 OK): `EmpresaDetalhesDTO`**

### `GET /api/empresa/meus-modulos`

| Detalhe       | Descrição                                                                      | Status Sucesso |
| :------------ | :----------------------------------------------------------------------------- | :------------- |
| **Descrição** | Lista os detalhes de todos os módulos que estão atualmente ativos para a empresa. | `200 OK`       |

**Resposta Sucesso (200 OK): `Set<Modulo>`** (Conjunto de entidades `Modulo` ativas).

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
**Resposta Sucesso (201 Created): `VendedorCriadoResponseDTO`**

#### `PUT /api/vendedores/{id}` **(NOVO DETALHE)**
**Requisição (Body - JSON): `VendedorUpdateRequestDTO`**
**Resposta Sucesso (200 OK): `VendedorResponseDTO`**

#### `GET /api/vendedores` (Listar Todos)
**Resposta Sucesso (200 OK): `List<VendedorResponseDTO>`**

#### `GET /api/vendedores/{id}/detalhes` (Detalhes e Histórico)
**Resposta Sucesso (200 OK): `VendedorDetalhadoResponseDTO`**

### 5.2. Gerenciamento de Vendas (`/api/vendas`)

| Método | URL           | Descrição                                                              |
| :----- | :------------ | :--------------------------------------------------------------------- |
| `POST` | `/api/vendas` | Lança uma nova venda e calcula o `valorComissaoCalculado`. |
| `GET`  | `/api/vendas` | Lista todas as vendas registradas na empresa logada.         |

#### `POST /api/vendas`
**Requisição (Body - JSON): `VendaRequestDTO`**
**Resposta Sucesso (201 Created): `Venda` (Entidade)**

### 5.3. Dashboard Gerencial (`/api/dashboard`)

#### `GET /api/dashboard/empresa` **(ATUALIZADO)**
**Descrição:** Retorna todas as métricas consolidadas (totais do mês, rankings, maiores/últimas vendas e histórico).
**Status Sucesso:** `200 OK`.

**Resposta Sucesso (200 OK): `DashboardResponseDTO`**

---

## 6. Acesso Geral - Usuário Logado (`/api/usuarios`) **(NOVO)**

Endpoints disponíveis para qualquer usuário autenticado gerenciar sua própria conta.

### `PUT /api/usuarios/me/senha` **(NOVO)**

**Descrição:** Permite que o usuário logado (Super Admin, Admin ou Vendedor) altere sua própria senha.
**Permissões:** Qualquer usuário autenticado.
**Status Sucesso:** `200 OK` (ou `204 No Content`).

**Requisição (Body - JSON): `AlterarSenhaRequestDTO` (Exemplo)**
**Resposta Sucesso (200 OK ou 204 No Content):** (Sem corpo)