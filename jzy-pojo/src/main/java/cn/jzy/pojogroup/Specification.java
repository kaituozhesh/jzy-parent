package cn.jzy.pojogroup;

import cn.jzy.pojo.TbSpecification;
import cn.jzy.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 * @Auther: 林俊豪
 * @Date: 2019/4/13
 */
public class Specification  implements Serializable {

    private TbSpecification specification;

    private List<TbSpecificationOption> specificationOptionList;

    public TbSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(TbSpecification specification) {
        this.specification = specification;
    }

    public List<TbSpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }
}
