package testLifecycleExtension;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.MDC;

import java.util.Optional;
import java.util.UUID;

public class TestLifecycleExtension implements BeforeEachCallback, AfterEachCallback, TestWatcher {

    private static final String TRACE_ID_KEY = "traceId";

    @Override
    public void beforeEach(ExtensionContext context) {
        // Кртиерий 2: Генерация короткого traceId (или полного UUID) перед каждым тестом
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put(TRACE_ID_KEY, traceId);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        // Очищаем MDC после теста, чтобы не загрязнять потоки
        MDC.remove(TRACE_ID_KEY);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        String currentTraceId = MDC.get(TRACE_ID_KEY);

        // Критерий 5: Снятие контекста и логов при падении и передача в Allure
        String attachmentText = String.format(
                "Тест упал. Системный контекст падения:\n" +
                        "ID отслеживания (X-Trace-Id): %s\n" +
                        "Класс теста: %s\n" +
                        "Метод: %s\n" +
                        "Причина падения: %s",
                currentTraceId,
                context.getRequiredTestClass().getName(),
                context.getRequiredTestMethod().getName(),
                cause.getMessage()
        );

        // Добавляем текстовый атачмент в Allure-отчет
        Allure.addAttachment("Контекст асинхронной цепочки (Trace/Kafka Log)", "text/plain", attachmentText);
    }

    // Остальные методы интерфейса TestWatcher оставляем пустыми по умолчанию
    @Override public void testDisabled(ExtensionContext context, Optional<String> reason) {}
    @Override public void testSuccessful(ExtensionContext context) {}
    @Override public void testAborted(ExtensionContext context, Throwable cause) {}
}

