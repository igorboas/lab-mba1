#### Criando uma entrada HTTP Gelf
após o login temos que criar uma entrada para receber as mensagens do cron job.
para fazer isso, você pode ir para: System -> Input

#### Criando o trabalho de milho
sabemos que tudo está configurado, nossa entrada Graylog está em execução, então temos que iniciar nossa fonte de dados
para registrar mensagens na instância Graylog.

inicie o cron job do K8s usando o seguinte comando
```
kubectl create -f cornJob.yaml
```
para exibir os detalhes do cron job, use o seguinte comando
```
kubectl conseguir emprego --watch
```

#### Verificando os Logs recebidos do CronJob:
Agora tudo deve funcionar bem, o Graylog Stack, CronJob.
temos apenas que verificar a mensagem recebida da seguinte forma: clique em Pesquisar

#### Criando um fluxo separado para mensagens CronJob:
até agora, fizemos um ótimo trabalho, temos tudo o que precisamos.
mas caso tenhamos várias entradas, e todas elas coloquem as mensagens no stream All-Messages,
vamos ficar um pouco confusos, então será difícil saber qual entrada enviou esta mensagem sem
filtragem. neste momento pense em criar seu próprio stream.

Para criar um fluxo para essa entrada específica, vá para fluxos, clique no botão Criar fluxo
e preencha o formulário da seguinte forma:
 
pressione Salvar, no meu caso eu chamei este fluxo de 'crinjob-1'.
depois disso, temos que gerenciar regras - devemos dizer ao graylog quais mensagens
deve estar em nosso fluxo.

clique em Gerenciar Regras -> Adicionar regra de fluxo e preencha o formulário da seguinte forma:

>no meu caso, estou dizendo ao graylog para colocar a mensagem recebida por "source"="alpine-k8s.org" no fluxo criado.

pressione Save e Go Streams, você listará todos os streams existentes:

> como você pode ver, nosso stream cronjob-1 foi criado, clique nele e você verá
todas as mensagens da fonte alpine-k8s.org que é nosso trabalho cron em execução

#### Onde ir
Graylog é muito flexível, suporta diferentes entradas de dados, você pode criar fluxos e anexá-los a uma determinada entrada/saída, ...
após este artigo, você poderá iniciar seu próprio Gaylog Stack e registrar dados nele,
para mais informações sobre o Graylog, dê uma olhada na [Documentação Oficial](https://docs.graylog.org/en/3.0/)

próximo artigo vamos usar Graylog com Spring Boot Application para demonstrar como
envie nossos logs de aplicativos para Graylog e como criar um painel para este aplicativo específico
para visualizar as métricas. ansioso :).