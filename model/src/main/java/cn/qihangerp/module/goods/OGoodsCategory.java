package cn.qihangerp.module.goods.domain;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 
 * @TableName o_goods_category
 */
@Data
public class OGoodsCategory implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分类编码
     */
    private String number;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 
     */
    private String remark;

    /**
     * 上架分类id
     */
    private Long parentId;
    private Long productTypeId;

    /**
     * 分类路径
     */
    private String path;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 图片
     */
    private String image;

    /**
     * 0正常  1删除
     */
    private Integer isDelete;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private String relationsJson;

    private static final long serialVersionUID = 1L;
}