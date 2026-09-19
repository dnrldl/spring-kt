package com.springkt.todo.presentation.web

import com.springkt.global.security.CurrentUserId
import com.springkt.global.web.SuccessResponse
import com.springkt.todo.application.usecase.CreateTodoUseCase
import com.springkt.todo.application.usecase.DeleteTodoUseCase
import com.springkt.todo.application.usecase.GetTodoUseCase
import com.springkt.todo.application.usecase.GetTodosUseCase
import com.springkt.todo.application.usecase.UpdateTodoUseCase
import com.springkt.todo.presentation.dto.CreateTodoRequest
import com.springkt.todo.presentation.dto.TodoResponse
import com.springkt.todo.presentation.dto.UpdateTodoRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/todos")
@Tag(name = "Todo", description = "Todo API")
class TodoController(
    private val createTodoUseCase: CreateTodoUseCase,
    private val getTodoUseCase: GetTodoUseCase,
    private val getTodosUseCase: GetTodosUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Todo 생성",
        security = [
            SecurityRequirement(name = "dpopAuth"),
            SecurityRequirement(name = "dpopProof"),
        ],
    )
    fun createTodo(
        @CurrentUserId userId: Long,
        @Valid @RequestBody request: CreateTodoRequest,
    ): SuccessResponse<TodoResponse> {
        val result = createTodoUseCase.createTodo(request.toCommand(userId))
        return SuccessResponse.ok(TodoResponse.from(result))
    }

    @GetMapping
    @Operation(
        summary = "Todo 목록 조회",
        security = [
            SecurityRequirement(name = "dpopAuth"),
            SecurityRequirement(name = "dpopProof"),
        ],
    )
    fun getTodos(
        @CurrentUserId userId: Long,
    ): SuccessResponse<List<TodoResponse>> {
        val response = getTodosUseCase.getTodos(userId).map(TodoResponse::from)
        return SuccessResponse.ok(response)
    }

    @GetMapping("/{todoId}")
    @Operation(
        summary = "Todo 단건 조회",
        security = [
            SecurityRequirement(name = "dpopAuth"),
            SecurityRequirement(name = "dpopProof"),
        ],
    )
    fun getTodo(
        @CurrentUserId userId: Long,
        @PathVariable todoId: Long,
    ): SuccessResponse<TodoResponse> {
        val result = getTodoUseCase.getTodo(userId, todoId)
        return SuccessResponse.ok(TodoResponse.from(result))
    }

    @PutMapping("/{todoId}")
    @Operation(
        summary = "Todo 수정",
        security = [
            SecurityRequirement(name = "dpopAuth"),
            SecurityRequirement(name = "dpopProof"),
        ],
    )
    fun updateTodo(
        @CurrentUserId userId: Long,
        @PathVariable todoId: Long,
        @Valid @RequestBody request: UpdateTodoRequest,
    ): SuccessResponse<TodoResponse> {
        val result = updateTodoUseCase.updateTodo(request.toCommand(userId, todoId))
        return SuccessResponse.ok(TodoResponse.from(result))
    }

    @DeleteMapping("/{todoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Todo 삭제",
        security = [
            SecurityRequirement(name = "dpopAuth"),
            SecurityRequirement(name = "dpopProof"),
        ],
    )
    fun deleteTodo(
        @CurrentUserId userId: Long,
        @PathVariable todoId: Long,
    ) {
        deleteTodoUseCase.deleteTodo(userId, todoId)
    }
}
