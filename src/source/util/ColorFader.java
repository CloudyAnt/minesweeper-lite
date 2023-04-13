package source.util;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Fadein or fadeout colors on components.
 * There are time control options, but not absolutely precise.
 */
public class ColorFader {

    private static ColorFader instance;

    private ColorFader(boolean lowPowerMode) {
        if (lowPowerMode) {
            this.multiAction = this::doChange;
            this.action = this::doChange;
        } else {
            this.multiAction = this::doFade;
            this.action = this::doFade;
        }
    }

    public static ColorFader getInstance() {
        if (instance == null) {
            instance = new ColorFader(false);
        }
        return instance;
    }

    private final MultiAction multiAction;
    private final Action action;

    // 3 ComponentList is enough for most cases
    private final ComponentList assistant0 = new ComponentList();
    private final ComponentList assistant1 = new ComponentList();
    private final ComponentList assistant2 = new ComponentList();

    public void fadeBG(List<? extends Component> components, ColorFadeData data) {
        ComponentList list = findAssistant();
        list.addAll(components);
        multiAction.act(list, data, Component::setBackground);
    }

    public void fadeFG(Component component, ColorFadeData data) {
        action.act(component, data, Component::setForeground);
    }

    public void fadeBG(Component component, ColorFadeData data) {
        action.act(component, data, Component::setBackground);
    }

    private ComponentList findAssistant() {
        ComponentList assistant;
        if (assistant0.getAvailable()) {
            assistant = assistant0;
        } else if (assistant1.getAvailable()) {
            assistant = assistant1;
        } else {
            assistant = assistant2.getAvailable() ? assistant2 : null;
        }

        if (assistant != null) {
            assistant.clear();
            return assistant;
        }

        return new ComponentList();
    }

    private void doFade(ComponentList components, ColorFadeData data, SetColor setColor) {
        Color[] gradients = data.getGradients();
        TaskExecutor.interval(null, data.getInterval(), data.getTotalFrames(), i -> {
            for (Component component : components) {
                setColor.set(component, gradients[i]);
            }
        }, components::enable, true);
    }

    private void doChange(ComponentList components, ColorFadeData data, SetColor setColor) {
        TaskExecutor.execute(() -> {
            Color c = data.getGradients()[data.getGradients().length - 1];
            for (Component component : components) {
                setColor.set(component, c);
            }
            components.enable();
        });
    }

    private void doFade(Component component, ColorFadeData data, SetColor setColor) {
        Color[] gradients = data.getGradients();
        TaskExecutor.interval(data.getInterval(), data.getTotalFrames(), i -> setColor.set(component, gradients[i]));
    }

    private void doChange(Component component, ColorFadeData data, SetColor setColor) {
        TaskExecutor.execute(() -> {
            Color c = data.getGradients()[data.getGradients().length - 1];
            setColor.set(component, c);
        });
    }

    private static class ComponentList extends ArrayList<Component> {
        private boolean available = true;

        public ComponentList() {
            super();
        }

        private boolean getAvailable() {
            if (available) {
                available = false;
                return true;
            }
            return false;
        }

        private void enable() {
            available = true;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }
    }

    private interface MultiAction {
        void act(ComponentList components, ColorFadeData data, SetColor setColor);
    }

    private interface Action {
        void act(Component component, ColorFadeData data, SetColor setColor);
    }

    private interface SetColor {
        void set(Component c, Color color);
    }
}
