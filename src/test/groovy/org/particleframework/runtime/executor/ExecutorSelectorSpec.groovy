/*
 * Copyright 2017 original authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.particleframework.runtime.executor

import com.sun.xml.internal.ws.util.CompletedFuture
import grails.gorm.transactions.Transactional
import io.reactivex.Single
import org.particleframework.context.ApplicationContext
import org.particleframework.context.annotation.Executable
import org.particleframework.core.annotation.NonBlocking
import org.particleframework.inject.ExecutableMethod
import org.particleframework.scheduling.executor.ExecutorSelector
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Singleton
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

/**
 * @author Graeme Rocher
 * @since 1.0
 */
class ExecutorSelectorSpec extends Specification {

    @Unroll
    void "test executor selector for method #methodName"() {
        given:
        ApplicationContext applicationContext = ApplicationContext.run("test")
        ExecutorSelector selector = applicationContext.getBean(ExecutorSelector)
        Optional<ExecutableMethod> method = applicationContext.findExecutableMethod(MyService, methodName)

        Optional<ExecutorService> executorService = selector.select(method.get())

        expect:
        executorService.isPresent() == present

        cleanup:
        applicationContext.stop()

        where:
        methodName              | present
        "someMethod"            | true
        "someNonBlockingMethod" | false
        "someReactiveMethod"    | false
        "someFutureMethod"      | false
    }


}

@Singleton
@Executable
class MyService {

    @Transactional
    void someMethod() {

    }

    @NonBlocking
    void someNonBlockingMethod() {

    }

    Single someReactiveMethod() {}

    CompletableFuture someFutureMethod() {}
}

