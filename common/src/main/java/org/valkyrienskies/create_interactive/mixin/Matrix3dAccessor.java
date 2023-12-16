package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.foundation.collision.Matrix3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Matrix3d.class, remap = false)
public interface Matrix3dAccessor {
    @Accessor("m00")
    void setM00(double m00);

    @Accessor("m01")
    void setM01(double m01);

    @Accessor("m02")
    void setM02(double m02);

    @Accessor("m10")
    void setM10(double m10);

    @Accessor("m11")
    void setM11(double m11);

    @Accessor("m12")
    void setM12(double m12);

    @Accessor("m20")
    void setM20(double m20);

    @Accessor("m21")
    void setM21(double m21);

    @Accessor("m22")
    void setM22(double m22);
}
