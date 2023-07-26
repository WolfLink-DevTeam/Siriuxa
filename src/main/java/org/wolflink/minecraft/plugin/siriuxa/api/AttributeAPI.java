package org.wolflink.minecraft.plugin.siriuxa.api;

import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.wolflink.common.ioc.Singleton;

@Singleton
public class AttributeAPI {
    /**
     * 给指定attribute直接增加给定值
     */
    public void addMonsterAttribute(Attributable attributable, String modifierName, Attribute attribute, double value) {
        AttributeInstance attributeInstance = attributable.getAttribute(attribute);
        if (attributeInstance != null) {
            attributeInstance.addModifier(new AttributeModifier(modifierName, value, AttributeModifier.Operation.ADD_NUMBER));
        }
    }

    /**
     * 给指定attribute的值乘以value
     */
    public void multiplyMonsterAttribute(Attributable attributable, String modifierName, Attribute attribute, double value) {
        AttributeInstance attributeInstance = attributable.getAttribute(attribute);
        if (attributeInstance != null) {
            attributeInstance.addModifier(new AttributeModifier(modifierName, value - 1, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        }
    }
}
