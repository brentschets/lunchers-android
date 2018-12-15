package hogent.be.lunchers.models

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Tag(
    val tagId: Int,
    @field:Json(name = "naam") val name: String,
    @field:Json(name = "kleur") val color: String
) : Parcelable