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

| Método | URL                          | Descrição                                                          |
| :----- | :--------------------------- | :----------------------------------------------------------------- |
| `POST` | `/api/superadmin/modulos`    | Cria um novo módulo no catálogo.                               |
| `GET`  | `/api/superadmin/modulos`    | Lista todos os módulos.                                     |
| `PUT`  | `/api/superadmin/modulos/{id}` | Atualiza um módulo existente.                               |
| `GET`  | `/api/superadmin/modulos/disponiveis` | Lista módulos com status `PRONTO_PARA_PRODUCAO`. |

### 2.2. Gerenciamento de Empresas (`/api/superadmin/empresas`)

| Método | URL                             | Descrição                                                                        |
| :----- | :------------------------------ | :------------------------------------------------------------------------------- |
| `POST` | `/api/superadmin/empresas`        | Cria nova empresa, o primeiro `ROLE_ADMIN` e associa módulos padrão. |
| `GET`  | `/api/superadmin/empresas`        | Lista todas as empresas.                                     |
| `GET`  | `/api/superadmin/empresas/{id}`   | Busca uma empresa pelo ID.                                     |
| `PUT`  | `/api/superadmin/empresas/{id}`   | Atualiza dados básicos da empresa (`nomeFantasia`, `cnpj`).          |
| `PUT`  | `/api/superadmin/empresas/{id}/modulos` | **Vende/Associa** um novo conjunto de módulos, substituindo os módulos ativos. |

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

*(Estrutura dos endpoints mantida)*

#### `GET /api/vendedores` (Listar Todos)
**Descrição:** Lista todos os vendedores da empresa com métricas agregadas (`qtdVendas`, `valorTotalVendas`).
**Resposta Sucesso (200 OK): `List<VendedorResponseDTO>`**

#### `GET /api/vendedores/{id}/detalhes` (Detalhes e Histórico)
**Descrição:** Busca o vendedor com todas as métricas e o `historicoRendimentos` mensal.
**Resposta Sucesso (200 OK): `VendedorDetalhadoResponseDTO`**

### 5.2. Gerenciamento de Vendas (`/api/vendas`)

*(Estrutura dos endpoints mantida)*

### 5.3. Dashboard Gerencial (`/api/dashboard`)

#### `GET /api/dashboard/empresa` **(ATUALIZADO)**
**Descrição:** Retorna todas as métricas consolidadas (totais do mês, rankings, maiores/últimas vendas e histórico).
**Resposta Sucesso (200 OK): `DashboardResponseDTO`**