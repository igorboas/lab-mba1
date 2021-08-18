const Redis = require('ioredis');

exports.cache = new Redis({
  host: process.env.REDIS_HOST || 'localhost',
  port: process.env.REDIS_PORT || 6379,
  db: process.env.REDIS_DB || 0,
  keyPrefix: 'template-service',
});

exports.keyify = (prefix, ...keys) => [prefix, ...keys].join(':');
