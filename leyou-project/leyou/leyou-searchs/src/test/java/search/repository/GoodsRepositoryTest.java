package search.repository;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.search.Repository.GoodsRepository;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pjo.Goods;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: GoodsRepositoryTest
 * @Description: TODO
 * @author: LiuGe
 * @date: 2020/7/12  15:44
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsRepositoryTest {
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SearchService searchService;
    @Test
    public void testCreateIndex(){
        template.createIndex(Goods.class);
        template.putMapping(Goods.class);
    }

    @Test
    public void loadData() throws JsonProcessingException {
        int page = 1;
        int rows = 100;
        int size = 0;
        do {
            // 查询spu信息
            PageResult<SpuBo> result = goodsClient.querySpuByPage(page, rows, true, null);
            List<SpuBo> spuList = result.getItems();
            if(CollectionUtils.isEmpty(spuList)){
                break;
            }
            // 构建成goods
            List<Goods> goodsList = new ArrayList<>();

            for (SpuBo spuBo : spuList) {
                Goods goods = searchService.bulidGdoods(spuBo);
                goodsList.add(goods);
            }
            // 存入索引库
            goodsRepository.saveAll(goodsList);
            // 翻页
            page++;
            size = spuList.size();
        } while (size == 100);
    }
}