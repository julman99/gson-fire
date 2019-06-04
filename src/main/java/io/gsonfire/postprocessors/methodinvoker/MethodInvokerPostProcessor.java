package io.gsonfire.postprocessors.methodinvoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.gsonfire.PostProcessor;
import io.gsonfire.annotations.ExposeMethodParam;
import io.gsonfire.annotations.ExposeMethodResult;
import io.gsonfire.gson.FireExclusionStrategy;
import io.gsonfire.gson.FireExclusionStrategyComposite;
import io.gsonfire.util.reflection.AnnotationInspector;

/**
 * @autor: julio
 */
public final class MethodInvokerPostProcessor<T> implements PostProcessor<T> {

	private static AnnotationInspector<Method, MappedMethod>	methodResultInspector	= new MappedMethodResultInspector();
	private static AnnotationInspector<Method, MappedMethod>	methodParamInspector	= new MappedMethodParamInspector();

	private final FireExclusionStrategy							serializationExclusionStrategy;
	private final boolean										enableMethodParam, enableMethodResult;

    public MethodInvokerPostProcessor() {
		this(false, true);
    }

    public MethodInvokerPostProcessor(FireExclusionStrategy serializationExclusionStrategy) {
		this(serializationExclusionStrategy, false, true);
	}

	public MethodInvokerPostProcessor(boolean enableMethodParam, boolean enableMethodResult) {
		this(new FireExclusionStrategyComposite(), enableMethodParam, enableMethodResult);
	}

	public MethodInvokerPostProcessor(FireExclusionStrategy serializationExclusionStrategy, boolean enableMethodParam, boolean enableMethodResult) {
        this.serializationExclusionStrategy = serializationExclusionStrategy;
		this.enableMethodParam = enableMethodParam;
		this.enableMethodResult = enableMethodResult;
    }

	/** @author piegames */
    @Override
    public void postDeserialize(T result, JsonElement src, Gson gson) {
		if (enableMethodParam && src.isJsonObject()) {
			JsonObject jsonObject = src.getAsJsonObject();
			for (MappedMethod m : methodParamInspector.getAnnotatedMembers(result.getClass(), ExposeMethodParam.class)) {
				if (!serializationExclusionStrategy.shouldSkipMethod(m)) {
					try {
						if (m.getConflictResolutionStrategy() == ExposeMethodResult.ConflictResolutionStrategy.OVERWRITE
								|| (m.getConflictResolutionStrategy() == ExposeMethodResult.ConflictResolutionStrategy.SKIP
										&& !jsonObject.has(m.getSerializedName()))) {
							Method method = m.getMethod();
							Parameter param = method.getParameters()[0];
							Object value = gson.fromJson(jsonObject.get(m.getSerializedName()), param.getType());
							method.invoke(result, value);
						}
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
    }

    @Override
    public void postSerialize(JsonElement result, T src, Gson gson) {
		if (enableMethodResult && result.isJsonObject()) {
            JsonObject jsonObject = result.getAsJsonObject();
			for (MappedMethod m : methodResultInspector.getAnnotatedMembers(src.getClass(), ExposeMethodResult.class)) {
                if(!serializationExclusionStrategy.shouldSkipMethod(m)) {
                    try {
                        if (m.getConflictResolutionStrategy() == ExposeMethodResult.ConflictResolutionStrategy.OVERWRITE || (m.getConflictResolutionStrategy() == ExposeMethodResult.ConflictResolutionStrategy.SKIP && !jsonObject.has(m.getSerializedName()))) {
                            Object value = m.getMethod().invoke(src);
                            jsonObject.add(m.getSerializedName(), gson.toJsonTree(value));
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
