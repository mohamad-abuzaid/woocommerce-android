package com.woocommerce.android.model

import android.os.Parcelable
import com.woocommerce.android.extensions.fastStripHtml
import com.woocommerce.android.extensions.formatDateToISO8601Format
import com.woocommerce.android.extensions.formatToYYYYmmDDhhmmss
import com.woocommerce.android.extensions.roundError
import com.woocommerce.android.ui.products.ProductBackorderStatus
import com.woocommerce.android.ui.products.ProductStatus
import com.woocommerce.android.ui.products.ProductStockStatus
import com.woocommerce.android.ui.products.ProductTaxStatus
import com.woocommerce.android.ui.products.ProductType
import kotlinx.android.parcel.Parcelize
import org.wordpress.android.fluxc.model.WCProductModel
import org.wordpress.android.util.DateTimeUtils
import java.math.BigDecimal
import java.util.Date

@Parcelize
data class Product(
    val remoteId: Long,
    val name: String,
    val description: String,
    val type: ProductType,
    val status: ProductStatus?,
    val stockStatus: ProductStockStatus,
    val backorderStatus: ProductBackorderStatus,
    val dateCreated: Date,
    val firstImageUrl: String?,
    val totalSales: Int,
    val reviewsAllowed: Boolean,
    val isVirtual: Boolean,
    val ratingCount: Int,
    val averageRating: Float,
    val permalink: String,
    val externalUrl: String,
    val price: BigDecimal?,
    val salePrice: BigDecimal?,
    val regularPrice: BigDecimal?,
    val taxClass: String,
    val manageStock: Boolean,
    val stockQuantity: Int,
    val sku: String,
    val length: Float,
    val width: Float,
    val height: Float,
    val weight: Float,
    val shippingClass: String,
    val isDownloadable: Boolean,
    val fileCount: Int,
    val downloadLimit: Int,
    val downloadExpiry: Int,
    val purchaseNote: String,
    val numVariations: Int,
    val images: List<Image>,
    val attributes: List<Attribute>,
    val dateOnSaleToGmt: Date?,
    val dateOnSaleFromGmt: Date?,
    val soldIndividually: Boolean,
    val taxStatus: ProductTaxStatus,
    val isSaleScheduled: Boolean
) : Parcelable {
    @Parcelize
    data class Image(
        val id: Long,
        val name: String,
        val source: String,
        val dateCreated: Date
    ) : Parcelable

    @Parcelize
    data class Attribute(
        val id: Long,
        val name: String,
        val options: List<String>,
        val isVisible: Boolean
    ) : Parcelable

    fun isSameProduct(product: Product): Boolean {
        return remoteId == product.remoteId &&
                stockQuantity == product.stockQuantity &&
                stockStatus == product.stockStatus &&
                status == product.status &&
                manageStock == product.manageStock &&
                backorderStatus == product.backorderStatus &&
                soldIndividually == product.soldIndividually &&
                sku == product.sku &&
                type == product.type &&
                numVariations == product.numVariations &&
                name.fastStripHtml() == product.name.fastStripHtml() &&
                description == product.description &&
                images == product.images &&
                taxClass == product.taxClass &&
                taxStatus == product.taxStatus &&
                isSaleScheduled == product.isSaleScheduled &&
                dateOnSaleToGmt == product.dateOnSaleToGmt &&
                dateOnSaleFromGmt == product.dateOnSaleFromGmt &&
                regularPrice == product.regularPrice &&
                salePrice == product.salePrice &&
                weight == product.weight &&
                length == product.length &&
                height == product.height &&
                width == product.width &&
                shippingClass == product.shippingClass
    }

    /**
     * Method merges the updated product fields edited by the user with the locally cached
     * [Product] model and returns the updated [Product] model.
     *
     * [newProduct] includes the updated product fields edited by the user.
     * if [newProduct] is not null, a copy of the stored [Product] model is created
     * and product fields edited by the user and added to this model before returning it
     *
     */
    fun mergeProduct(newProduct: Product?): Product {
        return newProduct?.let { updatedProduct ->
            this.copy(
                    description = updatedProduct.description,
                    name = updatedProduct.name,
                    sku = updatedProduct.sku,
                    manageStock = updatedProduct.manageStock,
                    stockStatus = updatedProduct.stockStatus,
                    stockQuantity = updatedProduct.stockQuantity,
                    backorderStatus = updatedProduct.backorderStatus,
                    soldIndividually = updatedProduct.soldIndividually,
                    regularPrice = updatedProduct.regularPrice,
                    salePrice = updatedProduct.salePrice,
                    isSaleScheduled = updatedProduct.isSaleScheduled,
                    dateOnSaleFromGmt = updatedProduct.dateOnSaleFromGmt,
                    dateOnSaleToGmt = updatedProduct.dateOnSaleToGmt,
                    taxStatus = updatedProduct.taxStatus,
                    taxClass = updatedProduct.taxClass,
                    length = updatedProduct.length,
                    width = updatedProduct.width,
                    height = updatedProduct.height,
                    weight = updatedProduct.weight,
                    shippingClass = updatedProduct.shippingClass
            )
        } ?: this.copy()
    }
}

fun Product.toDataModel(storedProductModel: WCProductModel?): WCProductModel {
    return (storedProductModel ?: WCProductModel()).also {
        it.remoteProductId = remoteId
        it.description = description
        it.name = name
        it.sku = sku
        it.manageStock = manageStock
        it.stockStatus = ProductStockStatus.fromStockStatus(stockStatus)
        it.stockQuantity = stockQuantity
        it.soldIndividually = soldIndividually
        it.backorders = ProductBackorderStatus.fromBackorderStatus(backorderStatus)
        it.regularPrice = regularPrice.toString()
        it.salePrice = salePrice.toString()
        it.length = length.toString()
        it.width = width.toString()
        it.weight = weight.toString()
        it.height = height.toString()
        it.shippingClass = shippingClass
        it.taxStatus = ProductTaxStatus.fromTaxStatus(taxStatus)
        it.taxClass = taxClass
        if (isSaleScheduled) {
            dateOnSaleFromGmt?.let { dateOnSaleFrom ->
                it.dateOnSaleFromGmt = dateOnSaleFrom.formatToYYYYmmDDhhmmss()
            }
            dateOnSaleToGmt?.let { dateOnSaleTo ->
                it.dateOnSaleToGmt = dateOnSaleTo.formatToYYYYmmDDhhmmss()
            }
        } else {
            it.dateOnSaleFromGmt = ""
            it.dateOnSaleToGmt = ""
        }
    }
}

fun WCProductModel.toAppModel(): Product {
    return Product(
        remoteId = this.remoteProductId,
        name = this.name,
        description = this.description,
        type = ProductType.fromString(this.type),
        status = ProductStatus.fromString(this.status),
        stockStatus = ProductStockStatus.fromString(this.stockStatus),
        backorderStatus = ProductBackorderStatus.fromString(this.backorders),
        dateCreated = DateTimeUtils.dateFromIso8601(this.dateCreated) ?: Date(),
        firstImageUrl = this.getFirstImageUrl(),
        totalSales = this.totalSales,
        reviewsAllowed = this.reviewsAllowed,
        isVirtual = this.virtual,
        ratingCount = this.ratingCount,
        averageRating = this.averageRating.toFloatOrNull() ?: 0f,
        permalink = this.permalink,
        externalUrl = this.externalUrl,
        price = this.price.toBigDecimalOrNull()?.roundError(),
        salePrice = this.salePrice.toBigDecimalOrNull()?.roundError(),
        regularPrice = this.regularPrice.toBigDecimalOrNull()?.roundError(),
        taxClass = this.taxClass,
        manageStock = this.manageStock,
        stockQuantity = this.stockQuantity,
        sku = this.sku,
        length = this.length.toFloatOrNull() ?: 0f,
        width = this.width.toFloatOrNull() ?: 0f,
        height = this.height.toFloatOrNull() ?: 0f,
        weight = this.weight.toFloatOrNull() ?: 0f,
        shippingClass = this.shippingClass,
        isDownloadable = this.downloadable,
        fileCount = this.getDownloadableFiles().size,
        downloadLimit = this.downloadLimit,
        downloadExpiry = this.downloadExpiry,
        purchaseNote = this.purchaseNote,
        numVariations = this.getNumVariations(),
        images = this.getImages().map {
            Product.Image(
                    it.id,
                    it.name,
                    it.src,
                    DateTimeUtils.dateFromIso8601(this.dateCreated) ?: Date()
            )
        },
        attributes = this.getAttributes().map {
            Product.Attribute(
                    it.id,
                    it.name,
                    it.options,
                    it.visible
            )
        },
        dateOnSaleToGmt = this.dateOnSaleToGmt.formatDateToISO8601Format(),
        dateOnSaleFromGmt = this.dateOnSaleFromGmt.formatDateToISO8601Format(),
        soldIndividually = this.soldIndividually,
        taxStatus = ProductTaxStatus.fromString(this.taxStatus),
        isSaleScheduled = this.dateOnSaleFromGmt.isNotEmpty() || this.dateOnSaleToGmt.isNotEmpty()
    )
}

/**
 * Returns the product as a [ProductReviewProduct] for use with the product reviews feature.
 */
fun WCProductModel.toProductReviewProductModel() =
        ProductReviewProduct(this.remoteProductId, this.name, this.permalink)
