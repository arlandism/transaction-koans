# transaction-koans

Try to resolve conflicts with different database transaction levels

## Usage

Set up your local db and run the tests
Example config:

```bash
mv config/database.edn.example config/database.edn
createdb transaction_koans
lein test
```

## Dependencies
- Postgres 9

## Roadmap
- Support more databases
- Support testing things besides isolation levels

## License

Copyright © 2015 Arlandis Word

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
