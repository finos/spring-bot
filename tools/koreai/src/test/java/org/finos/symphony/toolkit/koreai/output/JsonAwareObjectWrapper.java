package org.finos.symphony.toolkit.koreai.output;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import freemarker.core.CollectionAndSequence;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.IteratorModel;
import freemarker.ext.util.ModelFactory;
import freemarker.template.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * FROM: https://stackoverflow.com/questions/25156354/unwanted-quotes-in-substituted-freemarker-template-fields
 * @author moffrob
 *
 */
class JsonAwareObjectWrapper extends DefaultObjectWrapper {
    public JsonAwareObjectWrapper(Version incompatibleImprovements) {
        super(incompatibleImprovements);
    }


    private static class JsonNullNodeModel {

        static final ModelFactory FACTORY = new ModelFactory() {

            public TemplateModel create(Object object, ObjectWrapper wrapper) {
                return null;
            }
        };

    }

    private static class JsonTextNodeModel extends BeanModel
            implements TemplateScalarModel {
        static final ModelFactory FACTORY =
                new ModelFactory() {
                    public TemplateModel create(Object object, ObjectWrapper wrapper) {
                        return new JsonTextNodeModel(object, (BeansWrapper) wrapper);
                    }
                };

        JsonTextNodeModel(Object object, BeansWrapper wrapper) {
            super(object, wrapper);
        }

        /**
         * Returns the result of calling {@link TextNode#asText()} on the wrapped
         * TextNode.
         */
        public String getAsString() {
            return ((TextNode) object).asText();
        }
    }

    private static class JsonNumberNodeModel extends
            BeanModel
            implements
            TemplateNumberModel {
        static final ModelFactory FACTORY =
                new ModelFactory() {
                    public TemplateModel create(Object object, ObjectWrapper wrapper) {
                        return new JsonNumberNodeModel(object, (BeansWrapper) wrapper);
                    }
                };

        JsonNumberNodeModel(Object object, BeansWrapper wrapper) {
            super(object, wrapper);
        }

        @Override
        public Number getAsNumber() throws TemplateModelException {
            return ((NumericNode) object).numberValue();
        }
    }

    private static class JsonBooleanNodeModel extends BeanModel implements TemplateBooleanModel {
        static final ModelFactory FACTORY =
                new ModelFactory() {
                    public TemplateModel create(Object object, ObjectWrapper wrapper) {
                        return new JsonBooleanNodeModel(object, (BeansWrapper) wrapper);
                    }
                };


        JsonBooleanNodeModel(Object object, BeansWrapper wrapper) {
            super(object, wrapper);
        }

        @Override
        public boolean getAsBoolean() throws TemplateModelException {
            return ((BooleanNode) object).asBoolean();
        }
    }

    private static class JsonPOJONodeModel extends BeanModel {
        static final ModelFactory FACTORY =
                new ModelFactory() {
                    public TemplateModel create(Object object, ObjectWrapper wrapper) {
                        return new JsonPOJONodeModel(((POJONode) object).getPojo(), (BeansWrapper) wrapper);
                    }
                };


        JsonPOJONodeModel(Object object, BeansWrapper wrapper) {
            super(object, wrapper);
        }
    }

    private static class JsonObjectNodeModel extends BeanModel {
        static final ModelFactory FACTORY =
                new ModelFactory() {
                    public TemplateModel create(Object object, ObjectWrapper wrapper) {
                        return new JsonObjectNodeModel(object, (BeansWrapper) wrapper);
                    }
                };
        JsonObjectNodeModel(Object object, BeansWrapper wrapper) {
            super(object, wrapper);
        }

        @Override
        public TemplateModel get(String key) throws TemplateModelException {
            ObjectNode objectNode = (ObjectNode) object;
            final JsonNode jsonNode = objectNode.get(key);
            if (jsonNode != null)
                return wrap(jsonNode);
            else
                return null;
        }

        @Override
        public boolean isEmpty() {
            ObjectNode objectNode = (ObjectNode) object;
            return objectNode.size() == 0;
        }

        @Override
        public int size() {
            ObjectNode objectNode = (ObjectNode) object;
            return objectNode.size();
        }

        @Override
        public TemplateCollectionModel keys() {
            ObjectNode objectNode = (ObjectNode) object;
            return new IteratorModel(objectNode.fieldNames(), wrapper);
        }

        @Override
        public TemplateCollectionModel values() throws TemplateModelException {
            ObjectNode objectNode = (ObjectNode) object;

            List<JsonNode> values = new ArrayList<>(size());
            final Iterator<Map.Entry<String, JsonNode>> it = objectNode.fields();
            while (it.hasNext()) {
                JsonNode value = it.next().getValue();
                values.add(value);
            }
            return new CollectionAndSequence(new SimpleSequence(values, wrapper));
        }
    }

    private static class JsonArrayNodeModel extends BeanModel
            implements
            TemplateCollectionModel,
            TemplateSequenceModel {
        static final ModelFactory FACTORY =
                new ModelFactory() {
                    public TemplateModel create(Object object, ObjectWrapper wrapper) {
                        return new JsonArrayNodeModel(object, (BeansWrapper) wrapper);
                    }
                };


        private class Iterator
                implements
                TemplateSequenceModel,
                TemplateModelIterator {
            private int position = 0;

            public boolean hasNext() {
                return position < length;
            }

            public TemplateModel get(int index)
                    throws
                    TemplateModelException {
                return JsonArrayNodeModel.this.get(index);
            }

            public TemplateModel next()
                    throws
                    TemplateModelException {
                return position < length ? get(position++) : null;
            }

            public int size() {
                return JsonArrayNodeModel.this.size();
            }
        }

        private final int length;

        JsonArrayNodeModel(Object array, BeansWrapper wrapper) {
            super(array, wrapper);
            ArrayNode arrayNode = (ArrayNode) array;

            length = arrayNode.size();
        }

        @Override
        public TemplateModelIterator iterator() {
            return new Iterator();
        }

        @Override
        public TemplateModel get(int index) throws TemplateModelException {
            try {
                return wrap(((ArrayNode) object).get(index));
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        }

        @Override
        public int size() {
            return length;
        }

        @Override
        public boolean isEmpty() {
            return length == 0;
        }
    }

    @Override
    protected ModelFactory getModelFactory(Class<?> clazz) {
        if (TextNode.class.isAssignableFrom(clazz)) {
            return JsonTextNodeModel.FACTORY;
        } else if (NumericNode.class.isAssignableFrom(clazz)) {
            return JsonNumberNodeModel.FACTORY;
        } else if (BooleanNode.class.isAssignableFrom(clazz)) {
            return JsonBooleanNodeModel.FACTORY;
        } else if (POJONode.class.isAssignableFrom(clazz)) {
            return JsonPOJONodeModel.FACTORY;
        } else if (ArrayNode.class.isAssignableFrom(clazz)) {
            return JsonArrayNodeModel.FACTORY;
        } else if (ObjectNode.class.isAssignableFrom(clazz)) {
            return JsonObjectNodeModel.FACTORY;
        } else if (NullNode.class.isAssignableFrom(clazz)) {
            return JsonNullNodeModel.FACTORY;
        } else {
            return super.getModelFactory(clazz);
        }
    }
}