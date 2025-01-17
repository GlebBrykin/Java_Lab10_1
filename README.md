# Java_Lab10_1

* Создайте программу, которая моделирует работу ресторана с 3 столиками для посетителей. Посетители (потоки) могут входить и занимать столики. Когда все столики заняты, посетители ожидают в очереди. Используйте для реализации задачи методы `wait` и `notify`.
* Ваша задача - создать программу для параллельного скачивания нескольких файлов из сети с использованием `CompletableFuture`.
Создайте список URL-адресов файлов, которые вы хотите скачать.

Для каждого URL-адреса создайте отдельный `CompletableFuture`, который будет выполнять асинхронное скачивание файла. Используйте метод `CompletableFuture.allOf(...)` для ожидания завершения всех скачиваний.
