package com.leyou.search.Repository;

import com.leyou.search.pjo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
