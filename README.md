# Mock-balance
sandbox repository

## Prerequisites
- Docker
- Docker Compose
- Maven
- Java 17

## Running the Database in Docker
To start the database using Docker Compose, run the following command:
```bash
make db-up
```

To stop and remove the database container, run:
```bash
make db-downv
```

## Running Tests
If you prefer to run the tests locally using `Maven`
```bash
make test
```
## Running Tests in Docker
```bash
make docker-test
```
If you encounter permission issues with the target directory after running tests in Docker,
you can reset the permissions to your current user with the following command:
```bash
sudo chown -R $(id -u):$(id -g) $(pwd)/target
```

## current improvement

Наразі рішення оновлення балансів базується виключно на мові `Java` за допомогою асинхронної 
обробки через `ExecutorService`. Також реалізовано механізм ретраїв: якщо транзакція для певного
батчу зазнає невдачі, вона повторюється кілька разів з відповідними
інтервалами між спробами. На даний момент немає гарантії доставки.

В тестах можна поставити `1_000_000` мапу сгенерувати і запустити тест, відпрацьовує. Код саму задачу виконує.

## possible improvement

Необхідна гарантія доставки повідомлень. Використовуючи сторонні
системи для забезпечення гарантованої доставки, такі як `RabbitMQ` чи `Kafka`.
Якщо це платежі  то нам більш необхідна `Exactly Once` семантика.
Kafka її може давати. 

Наприклад можна вхідну мапу розбивати на батчі і сереалізувати та відпривити в топік чи в ексченж якщо це ребіт на обробку. 
Воркери можна скейлити при бажанні. Також звісно має бути транзакційність операцій(атомарність), також якщо вирішється проблема що якщо падіння
воркера чи у винекнені помилки, у разі якщо воркер не відправив `acknowledge` то таска перезапуститься.
Також необхідно налаштувати `моніторинг та алертинг`. 

На практиці платежі це складно і можливо необхідно відправляти в ребіт чи кафку 
окремо кожний юзер-баланс, остільки необхідні блокіровки транзакції, рівні ізоляцій. 