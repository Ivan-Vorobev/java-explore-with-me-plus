**Public**:

GET /events/{eventId}/comments - получение всех комментариев к событию

GET /events/{eventId}/comments/{commentId} получение конкретного комментария к событию

POST /users/{userId}/events/{eventId}/comments - оставить комментарий к событию (может лишь тот, у кого есть approved request)

GET /users/{userId}/comments - получение комментариев пользователя (только подтвержденные)


**Admin**:

DELETE /admin/comments/{commentId} - удаление комментария

PATCH /admin/comments/{commentId} - обновление статуса комментария (approved)

GET /admin/comments - получение комментариев пользователя (есть фильтрация - все или только подтвержденные)

GET /admin/comments/{commentId} - получение конкретного комментария по идентификатору