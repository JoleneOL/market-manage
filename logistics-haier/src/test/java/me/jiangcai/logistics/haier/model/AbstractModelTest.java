package me.jiangcai.logistics.haier.model;

import me.jiangcai.logistics.PersistingReadable;
import me.jiangcai.logistics.haier.HaierSupplier;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 测试目标，
 * 继承 AbstractModel 不仅仅支持序列化，而且支持toHTML 也支持从XML 或者JSON反序列化之后的再度序列化
 *
 * @author CJ
 */
public class AbstractModelTest {

    @Test
    public void go() throws IOException, ClassNotFoundException {
        go(OutInStore.class);
        go(OrderStatusSync.class);
        go(RejectInfo.class);
    }

    private void go(Class<? extends PersistingReadable> clazz) throws IOException, ClassNotFoundException {
        // HaierSupplier
        try (InputStream stream = new ClassPathResource("/" + clazz.getSimpleName() + ".xml").getInputStream()) {
            PersistingReadable originObject = HaierSupplier.xmlMapper.readValue(stream, clazz);
            System.out.println(originObject.toHTML());
            System.out.println();

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try (ObjectOutputStream outputStream = new ObjectOutputStream(buffer)) {
                outputStream.writeObject(originObject);
                outputStream.flush();
            }

            try (ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()))) {
                PersistingReadable current = (PersistingReadable) inputStream.readObject();
                assertThat(current)
                        .isEqualTo(originObject);
            }
        }
    }

}