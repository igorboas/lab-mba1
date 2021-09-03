const express = require('express');
const _ = require('lodash');

const logging = require('./logging');
const { cache, keyify } = require('./cache');

const templates = {
  'user.signup': vars => ({
    subject: 'Welcome!',
    content: `Hi ${vars.name}\nThanks for signing up to our product!\n\nRegards,\nThe Team`,
  }),
};

const app = express();
app.use(express.json());
app.use(logging);

app.post('/render', async function (req, res) {
  req.log.debug(req.headers);

  const template = _.get(req, 'body.template.name');
  const vars = _.get(req, 'body.template.vars');
  const key = keyify(template, Object.values(vars));

  req.log.info('created key: ' + key);

  const subject = await cache.get(key + '.subject');
  const content = await cache.get(key + '.content');
  if (subject && content) {
    req.log.info('responding with rendered content from cache');

    return res.status(200).send({ subject, content });
  }

  let rendered;
  try {
    req.log.info('rendering template: ' + template);

    rendered = templates[template](vars);
  } catch (error) {
    console.error(error);

    return res.status(500).send({ code: 'NoSuchTemplate', message: 'No such template!' });
  }

  try {
    req.log.info('adding just rendered template to cache...');

    await cache.setex(key + '.subject', 600, rendered.subject);
    await cache.setex(key + '.content', 600, rendered.content);
  } catch (error) {
    console.error(error);
  }

  req.log.info('returning the rendered template...');
  return res.status(200).send(rendered);
});

const port = process.env.PORT || 3000;

const server = app.listen(port, () => logging.logger.info(`listening on port ${port}`));

process.on('SIGINT', () => {
  logging.logger.info('received a SIGINT signal. going down...');

  server.close();
});
