package com.lesfurets.jenkins.unit.declarative

import com.lesfurets.jenkins.unit.BasePipelineTest

import static com.lesfurets.jenkins.unit.MethodSignature.method

@groovy.transform.InheritConstructors
abstract class DeclarativePipelineTest extends BasePipelineTest {

    def pipelineInterceptor = { Closure closure ->
        GenericPipelineDeclaration.binding = binding
        GenericPipelineDeclaration.createComponent(DeclarativePipeline, closure).execute(delegate)
    }

    def paramInterceptor = { Map desc ->
        addParam(desc.name, desc.defaultValue, false)
    }

    def stringInterceptor = { Map desc->
        if (desc) {
            // we are in context of paremetes { string(...)}
            if (desc.name) {
                addParam(desc.name, desc.defaultValue, false)
            }
            // we are in context of withCredentaials([string()..]) { }
            if(desc.variable) {
                return desc.variable
            }
        }
    }

    @Override
    void setUp() throws Exception {
        super.setUp()
        helper.registerAllowedMethod('booleanParam', [Map], paramInterceptor)
        helper.registerAllowedMethod('checkout', [Closure])
        helper.registerAllowedMethod('credentials', [String], { String credName ->
            return binding.getVariable('credentials')[credName]
        })
        helper.registerAllowedMethod('cron', [String])
        helper.registerAllowedMethod(method("pipeline", Closure), pipelineInterceptor)
        helper.registerAllowedMethod('pollSCM', [String])
        helper.registerAllowedMethod('script', [Closure])
        helper.registerAllowedMethod('skipDefaultCheckout')
        helper.registerAllowedMethod('string', [Map], stringInterceptor)
        helper.registerAllowedMethod('timeout', [Integer, Closure])
        helper.registerAllowedMethod('timestamps')
        binding.setVariable('credentials', [:])
        binding.setVariable('params', [:])
    }
}
