# Executar demonstração no Kubernetes

O guia a seguir ajudará você a executar este projeto de demonstração no Kubernetes.

### Pré-requisitos:

- Ambiente para Observabilidade OpenSource: Jaeger (traces), ELK (logs), Prometheus (métricas), primeira parte do laboratório
- Ambiente Dynatrace (inicie sua [avaliação gratuita](https://www.dynatrace.com/trial/)), segunda parte do laboratório
- Um cluster Kubernetes 
  - Qualquer cluster server, por exemplo, [AKS](https://azure.microsoft.com/en-us/services/kubernetes-service/) ou minikube rodando em uma máquina virtual

## 1. Configuração da Observabilidade Opensource

Primeiro precisamos deixar a nossa plataforma de observabilidade pronta. Para isso iremos instalar no próprio cluster as seguintes ferramentas:

- Jaegger, este será o nosso repositório de traces
- Graylog, este será o nosso repositório de logs
- Prometheus, esta será a nossa fonte de métricas
- Grafana, esta será a nossa interface para apresentação de dados

## 1. Configuração do Dynatrace

Para tirar o máximo proveito desta demonstração, precisamos aplicar algumas configurações ao seu ambiente Dynatrace.

### Ativar contexto de rastreamento W3C

O OpenTelemetry usa o [W3C Trace Context](https://www.w3.org/TR/trace-context) para propagação de contexto. Visite as configurações em **Configurações > Monitoramento de serviço do lado do servidor > Monitoramento profundo > Rastreamento distribuído** e habilite _Enviar cabeçalhos HTTP de contexto de rastreamento W3C_.

![Configurações de rastreamento distribuído](https://raw.githubusercontent.com/martinnirtl/otel-demo/master/docs/img/settings-distributedtracing.png)

### Adicionar regra para monitoramento de processo personalizado

Como todos os contêineres serão executados no mesmo host (Docker), o OneAgent naturalmente injetaria em todos os contêineres. Portanto, precisamos configurar uma regra de exclusão para impedir que o OneAgent injete no serviço de email e no gerador de carga. Lembre-se, o serviço de email será totalmente instrumentado com OpenTelemetry e o Load Generator apenas simula o tráfego.

Vá para **Configurações > Processos e contêineres > Regras de monitoramento de processos personalizados** e configure a seguinte regra:

> COPY o nome da variável daqui: `DISABLE_DEEP_MONITORING`

![Configurações de rastreamento distribuído](https://raw.githubusercontent.com/martinnirtl/otel-demo/master/docs/img/settings-customprocessmonitoringrules.png)

Tanto o Mail Service quanto o Load Generator têm a respectiva variável definida na imagem em tempo de compilação:

- Gerador de carga: https://github.com/martinnirtl/otel-demo/blob/master/services/loadgen/Dockerfile
- Serviço de correio: https://github.com/martinnirtl/otel-demo/blob/master/services/mail-service/Dockerfile

## 2. Configure a implantação

Antes de iniciarmos nossa demonstração, criaremos o namespace `otel` e um segredo do Kubernetes que permitirá que o coletor OpenTelemetry ingira períodos por meio da API Dynatrace. Copie o comando a seguir e substitua `<TENANT-BASEURL>` e `<API-TOKEN>` antes de criar o segredo executando o comando.

```bash
kubectl -n otel cria segredo genérico otel-collector-secret --from-literal "OTEL_ENDPOINT_URL=<TENANT-BASEURL>/api/v2/otlp" --from-literal "OTEL_AUTH_HEADER=Api-Token <API-TOKEN>"
```

> Crie um token por meio do menu Access Tokens com **Ingest OpenTelemetry traces** e, opcionalmente, **Write Configuration (API v1)** (se desejar executar a etapa 2.1) permissões atribuídas.

### 2.1 Criar Zona de Gerenciamento via Mônaco (opcional)

Se você deseja criar a Zona de Gerenciamento `OpenTelemetry Demo` contendo todas as entidades desta demonstração e obter uma introdução à ferramenta Monaco, confira este guia de 5/10 minutos [aqui](https://github.com/martinnirtl /otel-demo/tree/master/monaco).

## 3. Execute a demonstração

Antes de iniciarmos os serviços de demonstração, vamos implantar o coletor OpenTelemetry. Basicamente, você pode executar o comando de qualquer lugar do seu shell, mas se quiser copiá-lo, navegue até a pasta **kubernetes**. Depois execute o seguinte comando:

```bash
kubectl -n otel apply -f opentelemetry
```

Você pode verificar a implantação executando o seguinte comando:

```bash
kubectl -n otel obter tudo
```

Saída:

```bash
NOME PRONTO STATUS REINICIA IDADE
pod/otel-collector-57fdff48ff-xpzld 1/1 Em execução 0 10h

NOME TIPO CLUSTER-IP EXTERNO-IP PORTA(S) IDADE
service/otel-collector ClusterIP 10.0.163.171 <nenhum> 8888/TCP,4317/TCP 10h

NOME PRONTO ATUALIZADO IDADE DISPONÍVEL
deployment.apps/otel-collector 1/1 1 1 10h

NOME IDADE ATUAL DESEJADA PRONTO
replicaset.apps/otel-collector-57fdff48ff 1 1 1 10h
```

Isso criará alguns recursos do Kubernetes e implantará o coletor OpenTelemetry como uma implantação.
Em seguida, podemos implantar os serviços de demonstração no namespace `default`:

```bash
kubectl apply -f services
```

Novamente, você pode verificar a implantação executando o seguinte comando:

```bash
kubectl obter tudo
```

Saída:

```bash
NOME PRONTO STATUS REINICIA IDADE
pod/backend-64674d88db-6mds5 1/1 Em execução 0 10h
pod/loadgen-759f8999c5-tccqm 1/1 Em execução 0 10h
pod/mail-service-67d96955f-zcfk7 1/1 Em execução 0 10h
pod/mongo-5d5f76656-vpksg 1/1 Em execução 0 10h
pod/redis-5cdbbc6df6