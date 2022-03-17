# Laboratório de Observabilidade

Demo baseada em microserviços com o objetivo de demonstrar conceitos de Observabilidade, OpenTelemetry e como podemos atingir Observabilidade com Dynatrace

![Architecture](https://raw.githubusercontent.com/martinnirtl/otel-demo/master/docs/img/architecture-diagram.png)

## Explicando a aplicação

A aplicação em si não tem um propósito específico nem oferece uma bela interface do usuário. Ele apenas fornece um endpoint simples para os usuários se inscreverem com e-mail, nome e senha e envia um e-mail de confirmação posteriormente. Toda a comunicação do serviço é feita via HTTP.

Embora existam alguns serviços ao redor para tornar este exemplo mais representativo, os principais componentes são os seguintes:

1. Backend Service
2. Mail Service
3. Template Service

O serviço de back-end e o serviço de modelo serão monitorados por meio do OneAgent e criarão alguns intervalos personalizados do OpenTelemetry por meio de instrumentação manual. O serviço intermediário - o Mail Service - será instrumentado apenas com OpenTelemetry.

O procedimento de inscrição pode ser descrito em 6 passos simples:

1. Signup-endpoint é chamado com uma chamada HTTP-post com e-mail, nome e senha no corpo
2. Após a validação do endereço de e-mail, o usuário é armazenado no banco de dados do Mongo
3. O serviço de back-end chama o ponto de extremidade de envio do serviço de correio via HTTP-post para enviar um e-mail de confirmação de inscrição
4. O Mail Service chama o Template Service via gRPC para renderizar o corpo do e-mail
5. Após renderizar o email, o Template Service armazena o resultado no cache do Redis e retorna
6. Finalmente, o Mail Service chama um provedor de correio como serviço externo (por exemplo, Sendgrid) para enviar o e-mail

## Executar a Demo

Vamos executar a demo num Kubernetes, de acordo com o apresentado abaixo:

- [Kubernetes](https://github.com/aborigene/otel-demo/tree/master/kubernetes)

Para subir o ambiente vamos executar os passos abaixo

1. Setup do Graylog

Você precisa personalizar o valor GRAYLOG_HTTP_EXTERNAL_URI para que ele aponte para seu host local ou remoto, este é o endereço real do seu cluster ou o IP de acesso do seu minikube

```
  - name: GRAYLOG_HTTP_EXTERNAL_URI
    value: #your_remote_or_localhost_ip
```

Você deve alterar a senha padrão para fazer login na interface web do Graylog, para isso você deve executar o seguinte
comando em seu terminal

```
echo -n "Enter Password: " && head -1 </dev/stdin | tr -d '\n' | sha256sum | cut -d " " -f 1

```

Este comando solicitará que você insira sua senha e, em seguida, copie a senha com hash gerada para a variável de ambiente:

```
  - name: GRAYLOG_ROOT_PASSWORD_SHA2
    value: generate_hashed_password_aqui
```

Você pode verificar o arquivo de configuração do Graylog [graylog.conf](https://github.com/Graylog2/graylog2-server/blob/master/misc/graylog.conf) para mais detalhes.

#### Implantando a stack Graylog:

Para implantar a pilha, você cria um deploy kubernetes da seguinte maneira:

```
kubectl create ns graylog
kubectl create -f es-deploy.yaml
kubectl create -f mongo-deploy.yaml
kubectl create -f graylog-deploy.yaml
```

Para o acesso funcionar precisamos fazer um ajuste, precisamos pegar o IP real do meu serviço e atualizar a configuração, para isso execute o seguinte comando:

```
kubectl get all
```

Procure na lista o IP real, altere o arquivo graylog-deploy.yaml

```
  - name: GRAYLOG_HTTP_EXTERNAL_URI
    value: #IP_COLETADO_NO_PASSO_ANTERIOR
```

Você pode verificar a implantação usando o seguinte comando:

```
 kubectl get deploy
```
 
Você também pode verificar os pods criados por estes deploys:

```
 kubectl get pods
```
 
#### Faça login na interface web do Graylog:

Depois de iniciar o Graylog, você pode fazer login na interface web Graylog da seguinte maneira:

<br/><br/>![Página de login do Graylog](images/loging-page.PNG)

> Altere <your_ip_address> pelo seu endereço definido nos passos acima
> Usuário: admin
> Senha: a que você criou nos passos acima

#### Criando uma entrada TCP Gelf
Após o login temos que criar uma entrada para receber as mensagens de logs.
Para fazer isso:

- Acessar System -> Input
- Escolher GELF TCP da lista
- Preencher o nome dessa input como "FluentBit integration"
- Clicar em salvar

2. Setup do FluentBit

```
kubectl create namespace logging
kubectl create -f https://raw.githubusercontent.com/fluent/fluent-bit-kubernetes-logging/master/fluent-bit-service-account.yaml
kubectl create -f https://raw.githubusercontent.com/fluent/fluent-bit-kubernetes-logging/master/fluent-bit-role.yaml
kubectl create -f https://raw.githubusercontent.com/fluent/fluent-bit-kubernetes-logging/master/fluent-bit-role-binding.yaml
cd <base do repositorio>/kubernetes/fluentbit
```

Editar o arquivo fluent-bit-cm.yaml na sessão output-graylog.conf adicionar o ip interno do serviço do Graylog e a porta da input, como apresentado na interface, ficará algo assim:

```
  output-graylog.conf: |
    [OUTPUT]
        Name                    gelf
        Match                   *
        Host                    10.0.10.144
        Port                    12201
        Mode                    tcp
        Gelf_Short_Message_Key  log 
```

Executar os deploys:

```
kubectl apply -f fluent-bit-cm.yaml
kubectl apply -f fluent-bit-graylog-ds.yaml
```

2. Setup do Prometheus

```
cd <raiz do repositorio>/kubernetes/prometheus
kubectl apply -f clusterRole.yaml
kubectl apply -f config-map.yaml -n monitoring
kubectl apply -f prometheus-deployment.yaml -n monitoring
kubectl apply -f prometheus-service.yaml -n monitoring
```

Aguardar alguns minutos e verificar a IP/porta em que o serviço do Prometheus está exposto:

```
kubectl get service/prometheus-service -n monitoring 
```

Apontar o navegador para o IP/porta para acessar o serviço

3. Setup do Jaegger

```
cd <raiz do repositorio>/kubernetes/jaeger
kubectl.exe apply -f jaeger-all-in-one.yaml -n monitoring

```

Aguardar alguns minutos e verificar a IP/porta em que o serviço do Jaeger está exposto:

```
kubectl get service/jaeger-query -n monitoring 
```

Apontar o navegador para o IP/porta para acessar o serviço

4. Setup do Grafana

```
cd <raiz do repositorio>/kubernetes/grafana
kubectl apply -f grafana-datasource-config.yaml
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
```

Aguardar alguns minutos e verificar a IP/porta em que o serviço do Grafana está exposto:

```
kubectl get service/grafana -n monitoring 
```

Apontar o navegador para o IP/porta para acessar o serviço

5. Setup da Aplicação

### Tendo problemas ou dificuldades ou encontrou um bug?

Envie um email: [igor.simoes@dynatrace.com](mailto:igor.simoes@dynatrace.com), [krishnarupa@gmail.com](mailto:krishnarupa@gmail.com)
