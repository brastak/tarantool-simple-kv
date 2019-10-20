local log = require('log')

local storage = {
  init = function(self)
    log.info('Initialize application')
    box.once('init', function()
      box.schema.create_space('simple_kv')
      box.space.simple_kv:create_index(
        'primary', { type = 'hash', parts = {1, 'str'} }
      )
    end)
  end,

  -- returns persisted data or nil if specified key already exists
  create = function(self, key, data)
    log.debug('Insert new record with key [%s] and value [%s]', key, data)
    status, result = pcall(function(t) return box.space.simple_kv:insert(t) end, {key, data})
    if not status then
      log.error(result)
      -- insert throws error only on duplicated value found
      return nil
    end
    log.debug('New record successfully inserted: %s', result)
    return result[2]
  end,

  -- returns data for the specified key or nil if no key found
  retrieve = function(self, key)
    log.debug('Find record for key [%s]', key)
    local result = box.space.simple_kv:get{key}
    if result == nil then
      log.debug('No records found for key [%s]', key)
      return nil
    end
    return result[2]
  end,

  -- returns update data for the specified key or nil if no key found
  update = function(self, key, data)
    log.debug('Update record with key [%s] and value [%s]', key, data)
    local result = box.space.simple_kv:update(key, {{'=', 2, data}})
    if result == nil then
      log.debug('No records found for key [%s]', key)
      return nil
    end
    return result[2]
  end,

  -- returns true if data for the specified key was deleted or false if no key found
  delete = function(self, key)
    log.debug('Delete record with key [%s]', key)
    local result = box.space.simple_kv:delete{key}
    if result == nil then
      log.debug('No records found for key [%s]', key)
      return false
    end
    return true
  end
}

return storage